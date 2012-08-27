#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <hardware/lights.h>
#include <hardware/hardware.h>

#define BACKLIGHT "/sys/class/backlight/aml-bl/brightness"

static int set_light_backlight(struct light_device_t* dev,
        struct light_state_t const* state)
{
    int nwr, ret = -1, fd;
    char value[20];
    int light_level;
    light_level =state->color&0xff;
    
    fd = open(BACKLIGHT, O_RDWR);
    if (fd > 0) {
        nwr = sprintf(value, "%d\n", light_level);
        ret = write(fd, value, nwr);
        close(fd);
    }

    return ret;
}

static int open_lights(const struct hw_module_t* module, char const* name,
        struct hw_device_t** device)
{
    int res = -EINVAL;

    if (strcmp(LIGHT_ID_BACKLIGHT, name) == 0) {
        struct light_device_t *dev = malloc(sizeof(struct light_device_t));
        if (!dev) {
            return res;
        }

        memset(dev, 0, sizeof(*dev));
        dev->common.tag = HARDWARE_DEVICE_TAG;
        dev->common.version = 0;
        dev->common.module = (struct hw_module_t*)module;
        dev->common.close = NULL;
        dev->set_light = set_light_backlight;

        *device = (struct hw_device_t*)dev;
        res = 0;
    }

    return res;
}

static struct hw_module_methods_t lights_module_methods = {
    .open =  open_lights,
};

const struct hw_module_t HAL_MODULE_INFO_SYM = {
    .tag = HARDWARE_MODULE_TAG,
    .version_major = 1,
    .version_minor = 0,
    .id = LIGHTS_HARDWARE_MODULE_ID,
    .name = "Backlight",
    .author = "Amlogic",
    .methods = &lights_module_methods,
};
