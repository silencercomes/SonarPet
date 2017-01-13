package net.techcable.sonarpet.nms.switching;

import lombok.*;

import net.techcable.sonarpet.utils.NmsVersion;
import net.techcable.sonarpet.utils.Versioning;

@RequiredArgsConstructor
public enum SkeletonType {
    REGULAR(NmsVersion.EARLIEST),
    WITHER(NmsVersion.EARLIEST),
    STRAY(NmsVersion.v1_11_R1);

    private final NmsVersion firstVersion;

    public boolean isSupported() {
        return Versioning.NMS_VERSION.compareTo(firstVersion) >= 0;
    }
}
