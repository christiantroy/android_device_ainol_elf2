/*
 * Copyright (C) 2012 The Android Open Source Project
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
#include <errno.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#define LOG_TAG "PowerHAL"
#include <cutils/properties.h>
#include <utils/Log.h>

#include <hardware/hardware.h>
#include <hardware/power.h>

#define BOOSTPULSE_PATH "/sys/devices/system/cpu/cpufreq/ondemand/boostpulse"
#define SAMPLING_RATE_ONDEMAND "/sys/devices/system/cpu/cpufreq/ondemand/sampling_rate"
#define SAMPLING_RATE_SCREEN_ON "100000"
#define SAMPLING_RATE_SCREEN_OFF "400000"
#define MIN_CPUFREQ "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq"
#define MIN_CPUFREQ_ON "408000"
#define MIN_CPUFREQ_OFF "96000"
#define MAX_CPUFREQ "/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq"
#define MAX_CPUFREQ_ON "1320000"
#define MAX_CPUFREQ_OFF "1008000"

struct amlogic_power_module {
    struct power_module base;
    pthread_mutex_t lock;
    int boostpulse_fd;
    int boostpulse_warned;
    char sampling_rate_screen_on[PROPERTY_VALUE_MAX];
    char sampling_rate_screen_off[PROPERTY_VALUE_MAX];
    char min_cpu_screen_on[PROPERTY_VALUE_MAX];
    char min_cpu_screen_off[PROPERTY_VALUE_MAX];
    char max_cpu_screen_on[PROPERTY_VALUE_MAX];
    char max_cpu_screen_off[PROPERTY_VALUE_MAX];
};

static void sysfs_write(char *path, char *s)
{
    char buf[80];
    int len;
    int fd = open(path, O_WRONLY);

    if (fd < 0) {
        strerror_r(errno, buf, sizeof(buf));
        ALOGE("Error opening %s: %s\n", path, buf);
        return;
    }

    len = write(fd, s, strlen(s));
    if (len < 0) {
        strerror_r(errno, buf, sizeof(buf));
        ALOGE("Error writing to %s: %s\n", path, buf);
    }

    close(fd);
}

static int boostpulse_open(struct amlogic_power_module *amlogic)
{
    char buf[80];

    pthread_mutex_lock(&amlogic->lock);

    if (amlogic->boostpulse_fd < 0) {
        amlogic->boostpulse_fd = open(BOOSTPULSE_PATH, O_WRONLY);

        if (amlogic->boostpulse_fd < 0) {
            if (!amlogic->boostpulse_warned) {
                strerror_r(errno, buf, sizeof(buf));
                ALOGE("Error opening %s: %s\n", BOOSTPULSE_PATH, buf);
                amlogic->boostpulse_warned = 1;
            }
        }
    }

    pthread_mutex_unlock(&amlogic->lock);
    return amlogic->boostpulse_fd;
}

static void amlogic_power_hint(struct power_module *module, power_hint_t hint,
                            void *data)
{
    struct amlogic_power_module *amlogic = (struct amlogic_power_module *) module;
    char buf[80];
    int len;
    int duration = 1;

    switch (hint) {
    case POWER_HINT_INTERACTION:
    case POWER_HINT_CPU_BOOST:
        if (boostpulse_open(amlogic) >= 0) {
            if (data != NULL)
                duration = (int) data;

            snprintf(buf, sizeof(buf), "%d", duration);
            len = write(amlogic->boostpulse_fd, buf, strlen(buf));

            if (len < 0) {
                strerror_r(errno, buf, sizeof(buf));
                ALOGE("Error writing to %s: %s\n", BOOSTPULSE_PATH, buf);
            }
        }
        break;

    case POWER_HINT_VSYNC:
        break;

    default:
        break;
    }
}

static void amlogic_power_set_interactive(struct power_module *module, int on)
{
    struct amlogic_power_module *amlogic = (struct amlogic_power_module *) module;
    
    sysfs_write(SAMPLING_RATE_ONDEMAND,
            on ? amlogic->sampling_rate_screen_on : amlogic->sampling_rate_screen_off);
    
    sysfs_write(MIN_CPUFREQ,
            on ? amlogic->min_cpu_screen_on : amlogic->min_cpu_screen_off);
    
    sysfs_write(MAX_CPUFREQ,
            on ? amlogic->max_cpu_screen_on : amlogic->max_cpu_screen_off);
}

static void amlogic_power_init(struct power_module *module)
{
    struct amlogic_power_module *amlogic = (struct amlogic_power_module *) module;

    property_get("ro.sys.sampling_rate_on", amlogic->sampling_rate_screen_on, SAMPLING_RATE_SCREEN_ON);
    property_get("ro.sys.sampling_rate_off", amlogic->sampling_rate_screen_off, SAMPLING_RATE_SCREEN_OFF);
    sysfs_write(SAMPLING_RATE_ONDEMAND, amlogic->sampling_rate_screen_on);

    property_get("ro.sys.min_cpu_on", amlogic->min_cpu_screen_on, MIN_CPUFREQ_ON);
    property_get("ro.sys.min_cpu_off", amlogic->min_cpu_screen_off, MIN_CPUFREQ_OFF);
    sysfs_write(MIN_CPUFREQ, amlogic->min_cpu_screen_on);
    
    property_get("ro.sys.max_cpu_on", amlogic->max_cpu_screen_on, MAX_CPUFREQ_ON);
    property_get("ro.sys.max_cpu_off", amlogic->max_cpu_screen_off, MAX_CPUFREQ_OFF);
    sysfs_write(MAX_CPUFREQ, amlogic->max_cpu_screen_on);
}

static struct hw_module_methods_t power_module_methods = {
    .open = NULL,
};

struct amlogic_power_module HAL_MODULE_INFO_SYM = {
    base: {
        common: {
            tag: HARDWARE_MODULE_TAG,
            module_api_version: POWER_MODULE_API_VERSION_0_2,
            hal_api_version: HARDWARE_HAL_API_VERSION,
            id: POWER_HARDWARE_MODULE_ID,
            name: "Amlogic Power HAL",
            author: "The Android Open Source Project",
            methods: &power_module_methods,
        },
       init: amlogic_power_init,
       setInteractive: amlogic_power_set_interactive,
       powerHint: amlogic_power_hint,
    },

    lock: PTHREAD_MUTEX_INITIALIZER,
    boostpulse_fd: -1,
    boostpulse_warned: 0,
};
