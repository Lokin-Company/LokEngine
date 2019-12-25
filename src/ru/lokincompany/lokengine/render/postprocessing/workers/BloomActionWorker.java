package ru.lokincompany.lokengine.render.postprocessing.workers;

import org.lwjgl.opengl.GL11;
import ru.lokincompany.lokengine.loaders.ShaderLoader;
import ru.lokincompany.lokengine.render.Shader;
import ru.lokincompany.lokengine.render.enums.DrawMode;
import ru.lokincompany.lokengine.render.frame.BuilderProperties;
import ru.lokincompany.lokengine.render.frame.DisplayDrawer;
import ru.lokincompany.lokengine.render.frame.FrameBufferWorker;
import ru.lokincompany.lokengine.render.window.Window;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

public class BloomActionWorker extends PostProcessingActionWorker {

    private FrameBufferWorker frameBufferWorker1;
    private FrameBufferWorker frameBufferWorker2;
    private Shader filterShader;
    private Shader mixerShader;
    private Window window;

    private int blurAction;
    private BloomSettings bloomSettings;

    public BloomActionWorker(Window window) throws Exception {
        this.frameBufferWorker1 = new FrameBufferWorker(window.getResolution());
        this.frameBufferWorker2 = new FrameBufferWorker(window.getResolution());
        this.window = window;

        filterShader = ShaderLoader.loadShader("#/resources/shaders/bloom/BloomFilterVertShader.glsl", "#/resources/shaders/bloom/BloomFilterFragShader.glsl");
        mixerShader = ShaderLoader.loadShader("#/resources/shaders/bloom/BloomMixerVertShader.glsl", "#/resources/shaders/bloom/BloomMixerFragShader.glsl");

        window.getFrameBuilder().getBuilderProperties().useShader(filterShader);
        window.getCamera().updateProjection(window.getResolution().x, window.getResolution().y, 1);
        window.getFrameBuilder().getBuilderProperties().useShader(mixerShader);
        window.getCamera().updateProjection(window.getResolution().x, window.getResolution().y, 1);

        window.getFrameBuilder().getBuilderProperties().unUseShader();
    }

    @Override
    public String getName() {
        return "Bloom Action Worker";
    }

    public BloomSettings getBloomSettings() {
        return bloomSettings;
    }

    public void setBloomSettings(BloomSettings bloomSettings) {
        this.blurAction = bloomSettings.getBlurTexture(window);
        this.bloomSettings = bloomSettings;
    }

    @Override
    public int render(int sourceFrame) {
        if (blurAction != 0) {
            if (!frameBufferWorker1.getResolution().equals(window.getResolution())) {
                window.getFrameBuilder().getBuilderProperties().useShader(filterShader);
                window.getCamera().updateProjection(window.getResolution().x, window.getResolution().y, 1);
                window.getFrameBuilder().getBuilderProperties().useShader(mixerShader);
                window.getCamera().updateProjection(window.getResolution().x, window.getResolution().y, 1);
            }

            BlurActionWorker blur = (BlurActionWorker) window.getFrameBuilder().getPostProcessingActionWorker("Blur Action Worker");
            BuilderProperties builderProperties = window.getFrameBuilder().getBuilderProperties();

            frameBufferWorker1.bindFrameBuffer(DrawMode.Display, builderProperties);
            builderProperties.useShader(filterShader);
            filterShader.setUniformData("BrightnessLimit", bloomSettings.brightnessLimit);
            GL11.glClearColor(0, 0, 0, 0);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            DisplayDrawer.renderScreen(sourceFrame, window);

            frameBufferWorker1.unbindCurrentFrameBuffer();

            frameBufferWorker2.bindFrameBuffer(DrawMode.Display, builderProperties);
            glDisable(GL_ALPHA_TEST);

            GL11.glClearColor(0, 0, 0, 0);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            int blured = blur.onceRender(frameBufferWorker1.getTexture(), blurAction);

            builderProperties.useShader(mixerShader);
            mixerShader.setUniformData("Gamma", bloomSettings.gamma);
            mixerShader.setUniformData("Exposure", bloomSettings.exposure);
            DisplayDrawer.bindTexture("frame2", blured, 1, builderProperties);
            DisplayDrawer.renderScreen(sourceFrame, window);

            frameBufferWorker2.unbindCurrentFrameBuffer();
            return frameBufferWorker2.getTexture();

        }

        return sourceFrame;
    }
}