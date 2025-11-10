package com.hyu.common.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码工具类
 *
 * @author hyu
 */
@Slf4j
public class CaptchaUtils {

    /**
     * 验证码字符集
     */
    private static final char[] CODE_SEQUENCE = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 验证码长度
     */
    private static final int CODE_LENGTH = 4;

    /**
     * 图片宽度
     */
    private static final int WIDTH = 120;

    /**
     * 图片高度
     */
    private static final int HEIGHT = 40;

    /**
     * 干扰线数量
     */
    private static final int LINE_COUNT = 5;

    /**
     * 验证码过期时间（秒）
     */
    private static final int EXPIRE_TIME = 300;

    /**
     * 验证码结果
     */
    @Data
    public static class CaptchaResult {
        /**
         * 验证码ID
         */
        private String captchaId;

        /**
         * 验证码Base64图片
         */
        private String captchaImage;

        /**
         * 验证码过期时间（秒）
         */
        private Integer expireTime;

        /**
         * 验证码答案（仅用于测试，实际使用时不返回）
         */
        private String answer;
    }

    /**
     * 生成验证码
     *
     * @return 验证码结果
     */
    public static CaptchaResult generateCaptcha() {
        // 生成验证码ID
        String captchaId = "captcha_" + IdUtil.simpleUUID();

        // 生成验证码
        String code = generateCode();

        // 生成图片
        String base64Image = generateImage(code);

        CaptchaResult result = new CaptchaResult();
        result.setCaptchaId(captchaId);
        result.setCaptchaImage(base64Image);
        result.setExpireTime(EXPIRE_TIME);
        // result.setAnswer(code); // 生产环境不返回答案

        return result;
    }

    /**
     * 生成验证码字符串
     *
     * @return 验证码
     */
    private static String generateCode() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CODE_SEQUENCE[random.nextInt(CODE_SEQUENCE.length)]);
        }
        return code.toString();
    }

    /**
     * 生成验证码图片
     *
     * @param code 验证码
     * @return Base64图片
     */
    private static String generateImage(String code) {
        // 创建BufferedImage对象
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

        // 获取Graphics2D对象
        Graphics2D g = image.createGraphics();

        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 填充背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置字体
        g.setFont(new Font("Arial", Font.BOLD, 20));

        // 绘制验证码
        Random random = new Random();
        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));

            // 随机旋转角度
            double angle = (random.nextInt(60) - 30) * Math.PI / 180;

            // 旋转并绘制字符
            g.rotate(angle, 20 + i * 25, 25);
            g.drawString(String.valueOf(code.charAt(i)), 15 + i * 25, 25);
            g.rotate(-angle, 20 + i * 25, 25);
        }

        // 绘制干扰线
        for (int i = 0; i < LINE_COUNT; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.drawLine(random.nextInt(WIDTH), random.nextInt(HEIGHT),
                      random.nextInt(WIDTH), random.nextInt(HEIGHT));
        }

        // 绘制噪点
        for (int i = 0; i < 50; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillOval(random.nextInt(WIDTH), random.nextInt(HEIGHT), 1, 1);
        }

        g.dispose();

        // 转换为Base64
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            log.error("生成验证码图片失败", e);
            throw new RuntimeException("生成验证码图片失败");
        }
    }

    /**
     * 验证验证码
     *
     * @param captchaId   验证码ID
     * @param inputCode   用户输入的验证码
     * @param storedCode  存储的验证码
     * @return 是否验证成功
     */
    public static boolean validateCaptcha(String captchaId, String inputCode, String storedCode) {
        if (inputCode == null || storedCode == null) {
            return false;
        }

        // 不区分大小写比较
        return inputCode.equalsIgnoreCase(storedCode);
    }

    /**
     * 生成数字验证码（用于短信验证码等）
     *
     * @param length 长度
     * @return 验证码
     */
    public static String generateNumberCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(RandomUtil.randomInt(0, 10));
        }
        return code.toString();
    }

    /**
     * 生成6位数字验证码
     *
     * @return 验证码
     */
    public static String generateNumberCode() {
        return generateNumberCode(6);
    }
}