package com.manna.fobe.common.utils;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;

import java.awt.image.BufferedImage;

public class SvgUtils {

    public static BufferedImage readSvg(String url) {
        try {
            TranscoderInput input = new TranscoderInput(url);
            BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
            transcoder.addTranscodingHint(ImageTranscoder.KEY_WIDTH, 230f);
            transcoder.addTranscodingHint(ImageTranscoder.KEY_HEIGHT, 230f);
            transcoder.transcode(input, null);
            return transcoder.getBufferedImage();
        } catch (TranscoderException e) {
            throw new RuntimeException("SVG 이미지 변환 실패: " + url, e);
        }
    }

    static class BufferedImageTranscoder extends ImageTranscoder {
        private BufferedImage img;

        @Override
        public BufferedImage createImage(int width, int height) {
            return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        public void writeImage(BufferedImage img, TranscoderOutput output) {
            this.img = img;
        }

        public BufferedImage getBufferedImage() {
            return img;
        }
    }
}