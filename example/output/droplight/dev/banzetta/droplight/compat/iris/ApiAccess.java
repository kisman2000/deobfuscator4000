package dev.banzetta.droplight.compat.iris;

import net.irisshaders.iris.api.v0.*;

public class ApiAccess
{
    public static boolean isShaderPackInUse() {
        return IrisApi.getInstance().isShaderPackInUse();
    }
}
