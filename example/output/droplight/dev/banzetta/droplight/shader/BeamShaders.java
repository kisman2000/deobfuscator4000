package dev.banzetta.droplight.shader;

import java.util.function.*;
import dev.banzetta.droplight.*;
import org.apache.commons.lang3.exception.*;
import com.google.common.collect.*;
import net.minecraft.*;

public class BeamShaders
{
    private static Shader twoColorFadeShader;
    private static Shader twoColorFadeFlatShader;
    private static Shader twoColorFlatShader;
    public static final VertexFormat POSITION_TEX_COLOR0_COLOR1;
    
    public static Supplier<Shader> getTwoColorFade() {
        return (Supplier<Shader>)(() -> BeamShaders.twoColorFadeShader);
    }
    
    public static Supplier<Shader> getTwoColorFadeFlat() {
        return (Supplier<Shader>)(() -> BeamShaders.twoColorFadeFlatShader);
    }
    
    public static Supplier<Shader> getTwoColorFlat() {
        return (Supplier<Shader>)(() -> BeamShaders.twoColorFlatShader);
    }
    
    public static void init(final ResourceFactory provider) {
        if (BeamShaders.twoColorFadeShader != null && BeamShaders.twoColorFlatShader != null) {
            if (BeamShaders.twoColorFadeFlatShader != null) {
                return;
            }
        }
        try {
            BeamShaders.twoColorFadeShader = new Shader(provider, "droplight", BeamShaders.POSITION_TEX_COLOR0_COLOR1);
            BeamShaders.twoColorFadeFlatShader = new Shader(provider, "droplight_fade_flat", BeamShaders.POSITION_TEX_COLOR0_COLOR1);
            BeamShaders.twoColorFlatShader = new Shader(provider, "droplight_flat", BeamShaders.POSITION_TEX_COLOR0_COLOR1);
        }
        catch (final Exception e) {
            Droplight.LOGGER.error("Error loading shaders: {}", (Object)ExceptionUtils.getStackTrace((Throwable)e));
        }
    }
    
    public static boolean ready() {
        return BeamShaders.twoColorFadeShader != null && BeamShaders.twoColorFlatShader != null && BeamShaders.twoColorFadeFlatShader != null;
    }
    
    static {
        BeamShaders.twoColorFadeShader = null;
        BeamShaders.twoColorFadeFlatShader = null;
        BeamShaders.twoColorFlatShader = null;
        POSITION_TEX_COLOR0_COLOR1 = new VertexFormat(ImmutableMap.builder().put((Object)"Position", (Object)VertexFormats.POSITION_ELEMENT).put((Object)"UV0", (Object)VertexFormats.UV_ELEMENT).put((Object)"Color0", (Object)VertexFormats.COLOR_ELEMENT).put((Object)"Color1", (Object)VertexFormats.COLOR_ELEMENT).build());
    }
}
