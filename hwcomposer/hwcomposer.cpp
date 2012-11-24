/*
 * Copyright (C) 2010 The Android Open Source Project
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

//#define LOG_NDEBUG 0
#define HWC_REMOVE_DEPRECATED_VERSIONS 1

#define LOG_TAG "hwcomposer"
#include <hardware/hardware.h>

#include <fcntl.h>
#include <errno.h>

#include <cutils/log.h>
#include <cutils/atomic.h>

#include <hardware/hwcomposer.h>

#include <EGL/egl.h>

// for private_handle_t
#include "gralloc_priv.h"

#include <Amavutils.h>
#include <system/graphics.h>

/*****************************************************************************/

struct hwc_context_t {
    hwc_composer_device_1_t device;
    /* our private state goes below here */
    hwc_layer_1_t const* saved_layer;
    unsigned saved_transform;
    int saved_left;
    int saved_top;
    int saved_right;
    int saved_bottom;
};

static int hwc_device_open(const struct hw_module_t* module, const char* name,
        struct hw_device_t** device);

static struct hw_module_methods_t hwc_module_methods = {
    open: hwc_device_open
};

hwc_module_t HAL_MODULE_INFO_SYM = {
    common: {
        tag: HARDWARE_MODULE_TAG,
        version_major: 1,
        version_minor: 0,
        id: HWC_HARDWARE_MODULE_ID,
        name: "hwcomposer module",
        author: "Amlogic",
        methods: &hwc_module_methods,
        dso : NULL,
        reserved : {0},
    }
};

/*****************************************************************************/

static void hwc_overlay_compose(hwc_composer_device_1_t *dev, hwc_layer_1_t const* l) {
    int angle;
    struct hwc_context_t* ctx = (struct hwc_context_t*)dev;

    if ((ctx->saved_layer == l) &&
        (ctx->saved_transform == l->transform) &&
        (ctx->saved_left == l->displayFrame.left) &&
        (ctx->saved_top == l->displayFrame.top) &&
        (ctx->saved_right == l->displayFrame.right) &&
        (ctx->saved_bottom = l->displayFrame.bottom)) {
        return;
    }

    switch (l->transform) {
        case 0:
            angle = 0;
            break;
        case HAL_TRANSFORM_ROT_90:
            angle = 90;
            break;
        case HAL_TRANSFORM_ROT_180:
            angle = 180;
            break;
        case HAL_TRANSFORM_ROT_270:
            angle = 270;
            break;
        default:
            return;
    }

    amvideo_utils_set_virtual_position(l->displayFrame.left,
                                       l->displayFrame.top,
                                       l->displayFrame.right - l->displayFrame.left + 1,
                                       l->displayFrame.bottom - l->displayFrame.top + 1,
                                       angle);

    ctx->saved_layer = l;
    ctx->saved_transform = l->transform;
    ctx->saved_left = l->displayFrame.left;
    ctx->saved_top = l->displayFrame.top;
    ctx->saved_right = l->displayFrame.right;
    ctx->saved_bottom = l->displayFrame.bottom;
}

static void dump_layer(hwc_layer_1_t const* l) {
    ALOGD("\ttype=%d, flags=%08x, handle=%p, tr=%02x, blend=%04x, {%d,%d,%d,%d}, {%d,%d,%d,%d}",
            l->compositionType, l->flags, l->handle, l->transform, l->blending,
            l->sourceCrop.left,
            l->sourceCrop.top,
            l->sourceCrop.right,
            l->sourceCrop.bottom,
            l->displayFrame.left,
            l->displayFrame.top,
            l->displayFrame.right,
            l->displayFrame.bottom);
}

static int hwc_prepare(hwc_composer_device_1_t *dev, size_t numDisplays, hwc_display_contents_1_t** displays) {
    // Compat
    hwc_display_contents_1_t* list = NULL;
    if (numDisplays > 0) {
	list = displays[0];
    }

    if (list && (list->flags & HWC_GEOMETRY_CHANGED)) {
        for (size_t i=0 ; i<list->numHwLayers ; i++) {
            hwc_layer_1_t* l = &list->hwLayers[i];
            l->compositionType = HWC_FRAMEBUFFER;
        }
    }
    return 0;
}

static int hwc_set(hwc_composer_device_1_t *dev, size_t numDisplays, hwc_display_contents_1_t** displays)
{
    
    // Only support one display
    hwc_display_t dpy = displays[0]->dpy;
    hwc_surface_t sur = displays[0]->sur;
    hwc_display_contents_1_t* list = displays[0];

    if (list == NULL) {
        return 0;
    }

    for (size_t i=0 ; i<list->numHwLayers ; i++) {
        hwc_layer_1_t* l = &list->hwLayers[i];
        if (l->handle) {
            private_handle_t const* hnd = reinterpret_cast<private_handle_t const*>(l->handle);
            if (hnd->flags & private_handle_t::PRIV_FLAGS_VIDEO_OVERLAY) {
                hwc_overlay_compose(dev, l);
            }
        }
    }

    EGLBoolean sucess = eglSwapBuffers((EGLDisplay)dpy, (EGLSurface)sur);
    if (!sucess) {
        return HWC_EGL_ERROR;
    }
    return 0;
}

static int hwc_blank(struct hwc_composer_device_1 *dev, int disp, int blank)
{
    return 0;
}

static int hwc_eventControl(struct hwc_composer_device_1* dev, int dpy,
        int event, int enabled)
{
    switch (event) {
      case HWC_EVENT_VSYNC:
	  return 0;
    }
    return -EINVAL;
}

static int hwc_device_close(struct hw_device_t *dev)
{
    struct hwc_context_t* ctx = (struct hwc_context_t*)dev;
    if (ctx) {
        free(ctx);
    }
    return 0;
}

/*****************************************************************************/

static int hwc_device_open(const struct hw_module_t* module, const char* name,
        struct hw_device_t** device)
{
    int status = -EINVAL;
    if (!strcmp(name, HWC_HARDWARE_COMPOSER)) {
        struct hwc_context_t *dev;
        dev = (hwc_context_t*)malloc(sizeof(*dev));

        /* initialize our state here */
        memset(dev, 0, sizeof(*dev));

        /* initialize the procs */
        dev->device.common.tag = HARDWARE_DEVICE_TAG;
        dev->device.common.version = HWC_DEVICE_API_VERSION_1_0;
        dev->device.common.module = const_cast<hw_module_t*>(module);
        dev->device.common.close = hwc_device_close;

        dev->device.prepare = hwc_prepare;
        dev->device.set = hwc_set;
	dev->device.blank = hwc_blank;
	dev->device.eventControl = hwc_eventControl;

        *device = &dev->device.common;
        status = 0;
    }
    return status;
}
