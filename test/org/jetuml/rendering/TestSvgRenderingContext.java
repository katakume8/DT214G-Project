package org.jetuml.rendering;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Font;
import org.jetuml.geom.Alignment;
import org.jetuml.geom.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestSvgRenderingContext {

    @Test
    @DisplayName("Should test SvgRenderingContext constructor with valid arguments")
    void shouldTestConstructorWithValidArguments() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        String svgOutput = svgRenderingContext.create();

        assertEquals("<svg viewBox=\"43 43 114 114\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "<defs><filter id=\"shadow\" x=\"-10%\" y=\"-10%\">\n" +
                "  <feGaussianBlur in=\"SourceGraphic\" stdDeviation=\"1\" />\n" +
                "</filter></defs><g transform=\"translate(0.5,0.5)\" stroke-width=\"0.75\">\n" +
                "</g></svg>", svgOutput);
    }

    @Test
    @DisplayName("Should test SvgRenderingContext constructor with valid arguments")
    void shouldTestConstructorWithValidArgument1s() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        String svgOutput = svgRenderingContext.create();

        assertEquals("<svg viewBox=\"43 43 114 114\" xmlns=\"http://www.w3.org/2000/svg\">\n" +
                "<defs><filter id=\"shadow\" x=\"-10%\" y=\"-10%\">\n" +
                "  <feGaussianBlur in=\"SourceGraphic\" stdDeviation=\"1\" />\n" +
                "</filter></defs><g transform=\"translate(0.5,0.5)\" stroke-width=\"0.75\">\n" +
                "</g></svg>", svgOutput);
    }

    @Test
    @DisplayName("Should test the method strokeLine method")
    void shouldTestStrokeLineMethod() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Color svgColor = Color.BLACK;

        svgRenderingContext.strokeLine(1, 1, 1, 1, svgColor, LineStyle.SOLID);
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("<line x1=\"1\" y1=\"1\" x2=\"1\" y2=\"1\" stroke=\"black\"/>"),
                "SVG should include the expected <line> element");
    }

    @Test
    @DisplayName("Should test the method strokePath with valid arguments")
    void shouldTestStrokePathMethod() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Path path = new Path();
        Color svgColor = Color.BLACK;
        LineStyle svgLineStyle = LineStyle.DOTTED;

        svgRenderingContext.strokePath(path, svgColor, svgLineStyle);
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("<path d=\"\" stroke=\"black\" fill=\"none\" stroke-dasharray=\"3.0 3.0\"/>"),
                "SVG should include the dotted <path> element");
    }

    @Test
    @DisplayName("Should kill testStrokePath method mutation: Element mutations")
    void shouldKillStrokePathMutation() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Path path = new Path();
        MoveTo moveTo = new MoveTo(100, 100);
        LineTo lineTo = new LineTo(100, 100);
        QuadCurveTo curveTo = new QuadCurveTo();
        path.getElements().add(moveTo);
        path.getElements().add(lineTo);
        path.getElements().add(curveTo);
        Color svgColor = Color.BLACK;
        LineStyle svgLineStyle = LineStyle.DOTTED;

        svgRenderingContext.strokePath(path, svgColor, svgLineStyle);
        String svgOutput = svgRenderingContext.create();
        System.out.println(svgOutput);

        assertTrue(svgOutput.contains("stroke=\"black\""), "Path should have correct stroke");
        assertTrue(svgOutput.contains("fill=\"none\""), "Path should have correct fill");
        assertTrue(svgOutput.contains("stroke-dasharray=\"3.0 3.0\""),
                "Path should have correct stroke-dasharray");
        assertTrue(svgOutput.contains("M 100 100"), "Path should contain MoveTo command");
        assertTrue(svgOutput.contains("L 100 100"), "Path should contain LineTo command");
        assertTrue(svgOutput.contains("Q 0 0 0 0"), "Path should contain QuadCurveTo command");

    }

    @Test
    @DisplayName("Should test the method drawClosedPath with valid arguments")
    void shouldTestDrawClosedPathMethod() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Path path = new Path();
        Color svgColor = Color.WHITE;
        Color svgLineColor = Color.BLACK;
        DropShadow svgDropShadow = new DropShadow();

        svgRenderingContext.drawClosedPath(path, svgColor, svgLineColor, Optional.of(svgDropShadow));
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("<path d=\"\" stroke=\"none\" fill=\"lightGray\"" +
                        "  transform=\"translate(2 2)\" style=\"filter:url(#shadow);\"/>"),
                "SVG should include the shadowed lightGray path element");

        assertTrue(svgOutput.contains("<path d=\"\" stroke=\"black\" fill=\"white\"/>"),
                "SVG should include the black fill path element");
    }


    @Test
    @DisplayName("Should test the method drawClosedPath with non supported arguments")
    void shouldTestClosedPathMethodWithUnsupportedColors() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Path path = new Path();
        Color svgColor = Color.ALICEBLUE;
        Color svgLineColor = Color.BLACK;
        DropShadow svgDropShadow = new DropShadow();

        svgRenderingContext.drawClosedPath(path, svgColor, svgLineColor, Optional.of(svgDropShadow));
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("fill=\"rgb(90%, 90%, 60%)\""),
                "Path should default to rgb(90%, 90%, 60%) if color is not supported");
    }

    @Test
    @DisplayName("Should kill drawClosedPath mutation: pfFillColor == false")
    void shouldKillClosedPathMutation() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Path path = new Path();
        Color svgColor = Color.BLACK;
        Color svgLineColor = Color.BLACK;
        DropShadow svgDropShadow = new DropShadow();

        svgRenderingContext.drawClosedPath(path, svgColor, svgLineColor, Optional.of(svgDropShadow));
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("<path d=\"\" stroke=\"black\" fill=\"black\"/>"),
                "SVG should include the black fill path element");
    }

    @Test
    @DisplayName("Should test the method drawRectangle with valid arguments")
    void shouldTestDrawRectangleMethod() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Color svgColor = Color.WHITE;
        Color svgLineColor = Color.BLACK;
        DropShadow svgDropShadow = new DropShadow();

        svgRenderingContext.drawRectangle(rectangle, svgColor, svgLineColor, Optional.of(svgDropShadow));
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("<rect width=\"100\" height=\"100\" x=\"52\" y=\"52\" " +
                        "stroke=\"none\" fill=\"lightgray\" style=\"filter:url(#shadow);\"/>"),
                "SVG should include the lightgray shadow rectangle");

        assertTrue(svgOutput.contains("<rect width=\"100\" height=\"100\" x=\"50\" y=\"50\"" +
                        " stroke=\"black\" fill=\"white\"/>"),
                "SVG should include the white rectangle with black stroke");
    }

    @Test
    @DisplayName("Should test the method drawOval with valid arguments")
    void shouldTestDrawOvalMethod() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Color svgColor = Color.WHITE;
        Color svgLineColor = Color.BLACK;
        DropShadow svgDropShadow = new DropShadow();

        svgRenderingContext.drawOval(
                2, 2, 100, 100, svgColor, svgLineColor, Optional.of(svgDropShadow));
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("<ellipse rx=\"50\" ry=\"50\" cx=\"54\" cy=\"54\"" +
                        " stroke=\"none\" fill=\"lightgray\" style=\"filter:url(#shadow);\"/>"),
                "SVG should include the shadowed lightgray ellipse");

        assertTrue(svgOutput.contains("<ellipse rx=\"50\" ry=\"50\" cx=\"52\" cy=\"52\"" +
                        " stroke=\"black\" fill=\"white\"/>"),
                "SVG should include the main white ellipse with black stroke");
    }

    @Test
    @DisplayName("Should kill draw oval method mutation: Fill color != Color.WHITE")
    void shouldKillDrawOvalMethodMutation() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Color svgColor = Color.BLACK;
        Color svgLineColor = Color.BLACK;
        DropShadow svgDropShadow = new DropShadow();

        svgRenderingContext.drawOval(
                2, 2, 100, 100, svgColor, svgLineColor, Optional.of(svgDropShadow));
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("<ellipse rx=\"50\" ry=\"50\" cx=\"54\" cy=\"54\"" +
                        " stroke=\"none\" fill=\"lightgray\" style=\"filter:url(#shadow);\"/>"),
                "SVG should include the shadowed lightgray ellipse");

        assertTrue(svgOutput.contains("<ellipse rx=\"50\" ry=\"50\" cx=\"52\" cy=\"52\"" +
                        " stroke=\"black\" fill=\"black\"/>"),
                "SVG should include the main white ellipse with black stroke");
    }

    @Test
    @DisplayName("Should test the method strokeArc with valid arguments")
    void shouldTestStrokeArcMethod() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Color svgColor = Color.BLACK;

        svgRenderingContext.strokeArc(10, 10, 2, 3, 2, svgColor);
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("<path d=\"M 10 12 A 2 2 0 1 1 10 12\" stroke=\"black\" fill=\"none\"/>"),
                "SVG should include the expected arc path element");
    }

    @Test
    @DisplayName("Should kill strokeArc mutations: Operator mutations")
    void shouldKillStrokeArcMutations() {
        SvgRenderingContext ctx = new SvgRenderingContext(new Rectangle(0, 0, 200, 200));

        int centerX = 50;
        int centerY = 50;
        int radius = 10;

        int[] startAngles = {0, 45, 90, 180};
        int[] lengths = {30, 90, 180, 270};

        for (int startAngle : startAngles) {
            for (int length : lengths) {
                ctx.strokeArc(centerX, centerY, radius, startAngle, length, Color.BLACK);

                double startRad = Math.toRadians(startAngle);
                double endRad = Math.toRadians((startAngle - length) % 360);

                int x1 = (int) (centerX + Math.round(Math.sin(startRad) * radius));
                int y1 = (int) (centerY + Math.round(Math.cos(startRad) * radius));
                int x2 = (int) (centerX + Math.round(Math.sin(endRad) * radius));
                int y2 = (int) (centerY + Math.round(Math.cos(endRad) * radius));

                String expectedPath = String.format(
                        "<path d=\"M %d %d A %d %d 0 1 1 %d %d\" stroke=\"black\" fill=\"none\"/>",
                        x1, y1, radius, radius, x2, y2
                );

                String svgOutput = ctx.create();
                assertTrue(svgOutput.contains(expectedPath),
                        String.format("SVG should contain arc path for startAngle=%d, length=%d", startAngle, length));
            }
        }
    }

    @Test
    @DisplayName("Should test the method drawRoundRectangle with valid arguments")
    void shouldTestDrawRoundRectangleMethod() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Color svgColor = Color.WHITE;
        Color svgLineColor = Color.BLACK;
        DropShadow svgDropShadow = new DropShadow();

        svgRenderingContext.drawRoundedRectangle(rectangle, svgLineColor, svgColor, Optional.of(svgDropShadow));
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("<rect width=\"100\" height=\"100\" x=\"52\" y=\"52\" rx=\"10\" ry=\"10\"" +
                        " stroke=\"none\" fill=\"lightGray\" style=\"filter:url(#shadow);\"/>"),
                "SVG should include the rounded shadow rectangle");

        assertTrue(svgOutput.contains("<rect width=\"100\" height=\"100\" x=\"50\" y=\"50\" rx=\"10\" ry=\"10\"" +
                        " stroke=\"black\" fill=\"white\"/>"),
                "SVG should include the rounded main rectangle");
    }

    @Test
    @DisplayName("Should test the method drawText with valid arguments")
    void shouldTestDrawTextMethod() {
        String text = "Testing";
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Font font = new Font("Arial Italic", 12);
        Alignment alignment = Alignment.CENTER;
        Color svgColor = Color.BLACK;
        FontDimension dimension = new FontDimension(12, 12);

        svgRenderingContext.drawText(text, rectangle, alignment, svgColor, font, dimension);
        String svgOutput = svgRenderingContext.create();
        System.out.println(svgOutput);

        assertTrue(svgOutput.contains(">Testing</text>"), "SVG should include the correct text content");
        assertTrue(svgOutput.contains("x=\"100\"")
                && svgOutput.contains("y=\"138\""), "SVG should have correct coordinates for text");
        assertTrue(svgOutput.contains("font-style=\"italic\""), "SVG should have italic text");

    }

    @Test
    @DisplayName("Should kill drawText method mutations: Bold text")
    void shouldKillDrawTextMethodMutationsBoldText() {
        String text = "Testing";
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Font font = new Font("Arial Bold", 12);
        Alignment alignment = Alignment.CENTER;
        Color svgColor = Color.BLACK;
        FontDimension dimension = new FontDimension(12, 12);

        svgRenderingContext.drawText(text, rectangle, alignment, svgColor, font, dimension);
        String svgOutput = svgRenderingContext.create();

        assertTrue(svgOutput.contains("font-weight=\"bold\""), "SVG should have bold text");
    }

    @Test
    @DisplayName("Should kill drawText method mutations: Double Subtraction")
    void shouldKillFontSizeSubtractionMutation() {
        Rectangle rectangle = new Rectangle(50, 50, 100, 100);
        SvgRenderingContext svgRenderingContext = new SvgRenderingContext(rectangle);
        Font font = new Font("Arial", 12);
        Alignment alignment = Alignment.CENTER;
        Color color = Color.BLACK;
        FontDimension dimension = new FontDimension(12, 12);
        double fontAdjustment = 0.25;

        svgRenderingContext.drawText("Testing", rectangle, alignment, color, font, dimension);
        String svgOutput = svgRenderingContext.create();
        double expectedSize = font.getSize() - fontAdjustment;

        assertTrue(svgOutput.contains(String.format("font-size=\"%.2fpx\"", expectedSize)),
                "SVG should contain the correct font-size");
    }
}
