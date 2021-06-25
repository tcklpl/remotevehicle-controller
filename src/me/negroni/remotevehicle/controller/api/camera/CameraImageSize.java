package me.negroni.remotevehicle.controller.api.camera;

public enum CameraImageSize {

    SIZE_96X96(1, 96, 96),
    SIZE_QQVGA(2, 160, 120),
    SIZE_QCIF(3, 176, 144),
    SIZE_HQVGA(4, 240, 176),
    SIZE_240X240(5, 240, 240),
    SIZE_QVGA(6, 320, 240),
    SIZE_CIF(7, 400, 296),
    SIZE_HVGA(8, 480, 320),
    SIZE_VGA(9, 640, 480),
    SIZE_SVGA(10, 800, 600),
    SIZE_XGA(11, 1024, 768),
    SIZE_HD(12, 1280, 720),
    SIZE_SXGA(13, 1280, 1024),
    SIZE_UXGA(14, 1600, 1200)
    ;

    private final int code, width, height;

    CameraImageSize(int code, int width, int height) {
        this.code = code;
        this.width = width;
        this.height = height;
    }

    public int getCode() {
        return code;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
