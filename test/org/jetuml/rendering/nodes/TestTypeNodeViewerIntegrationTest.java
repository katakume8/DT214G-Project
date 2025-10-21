package org.jetuml.rendering.nodes;


import javafx.scene.paint.Color;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.geom.Rectangle;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.ClassDiagramRenderer;
import org.jetuml.rendering.GraphicsRenderingContext;
import org.jetuml.rendering.LineStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestTypeNodeViewerIntegrationTest {

    private GraphicsRenderingContext context;
    private TypeNodeRenderer renderer;
    private ClassNode node;

    @BeforeEach
    void setup() {
        this.context = mock(GraphicsRenderingContext.class);
        ClassDiagramRenderer diagram = new ClassDiagramRenderer(new Diagram(DiagramType.CLASS));
        this.renderer = new TypeNodeRenderer(diagram);
        this.node = new ClassNode();
    }

    @Test
    @DisplayName("Should test the draw method working as expected")
    void shouldTestDrawMethod() {
        node.setName("Class");
        node.setAttributes("x:int");
        node.setMethods("foo():void");

        renderer.draw(node, context);

        verify(context).drawRectangle(any(Rectangle.class),
                any(Color.class),
                any(Color.class),
                any(Optional.class));

        verify(context).drawText(contains("Class"), any(Rectangle.class), any(), any(), any(), any());
        verify(context).drawText(contains("x:int"), any(Rectangle.class), any(), any(), any(), any());
        verify(context).drawText(contains("foo():void"), any(Rectangle.class), any(), any(), any(), any());

        verify(context, atLeast(2)).strokeLine(anyInt(), anyInt(), anyInt(), anyInt(),
                any(Color.class), eq(LineStyle.SOLID));
    }


    @Test
    @DisplayName("Should kill draw name mutation: Removed call")
    void shouldKillDrawNameMutation() {
        node.setName("EmptyClass");
        node.setAttributes("");
        node.setMethods("");

        renderer.draw(node, context);

        verify(context).drawRectangle(any(Rectangle.class), any(), any(), any());

        verify(context, atLeastOnce()).drawText(
                anyString(),
                any(Rectangle.class),
                any(),
                any(),
                any(),
                any()
        );
    }


    @Test
    @DisplayName("Should kill draw attribute mutation: Removed call")
    void shouldKillDrawAttributeMutation() {
        node.setName("ClassName");
        node.setAttributes("x:int");
        node.setMethods("");

        renderer.draw(node, context);

        verify(context, atLeastOnce()).drawText(
                contains("x:int"),
                any(Rectangle.class),
                any(),
                any(),
                any(),
                any()
        );
    }


    @Test
    @DisplayName("Should kill draw method mutation: Removed call")
    void shouldKillDrawMethodMutation() {
        node.setName("ClassName");
        node.setAttributes("x:int");
        node.setMethods("foo():void");

        renderer.draw(node, context);

        verify(context, atLeastOnce()).drawText(
                contains("foo():void"),
                any(Rectangle.class),
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    @DisplayName("Should kill draw method mutation: Removed call")
    void shouldKillDrawMethodMutationNoAttributes() {
        node.setName("ClassName");
        node.setAttributes("");
        node.setMethods("foo():void");

        renderer.draw(node, context);

        verify(context, atLeastOnce()).drawText(
                contains("foo():void"),
                any(Rectangle.class),
                any(),
                any(),
                any(),
                any()
        );
    }

    @Test
    @DisplayName("Should kill draw rectangle mutation: Removed call")
    void shouKillDrawRectangleMutation() {
        node.setName("EmptyClass");
        node.setAttributes("");
        node.setMethods("");

        renderer.draw(node, context);

        verify(context, atLeastOnce()).drawRectangle(any(Rectangle.class), any(), any(), any());
    }

    @Test
    @DisplayName("Should kill draw method mutation: if(attributeBoxHeight > 0) boundary and false")
    void shouldKillDrawAttributeBoxHeightMutation() {
        node.setName("AttrClass");
        node.setAttributes("x:int");
        node.setMethods("");

        renderer.draw(node, context);

        verify(context).drawRectangle(any(Rectangle.class),
                any(Color.class),
                any(Color.class),
                any(Optional.class)
        );

        verify(context, times(1)).strokeLine(anyInt(),
                anyInt(),
                anyInt(),
                anyInt(),
                any(Color.class),
                eq(LineStyle.SOLID));
    }

    @Test
    @DisplayName("Should kill draw method mutation: boundary and false conditions")
    void shouldKillDrawMethodBoxHeightMutations() {
        node.setName("EmptyMethodClass");
        node.setAttributes("");
        node.setMethods("");

        renderer.draw(node, context);

        verify(context, never()).strokeLine(anyInt(), anyInt(), anyInt(), anyInt(),
                eq(ColorScheme.get().stroke()), eq(LineStyle.SOLID));

        node.setMethods("+getX():int");

        renderer.draw(node, context);

        verify(context, atLeastOnce()).strokeLine(anyInt(), anyInt(), anyInt(), anyInt(),
                eq(ColorScheme.get().stroke()), eq(LineStyle.SOLID));
    }

    @Test
    @DisplayName("Should kill stroke line mutation: Removed call")
    void shouldKillStrokeLineMutation() {
        node.setName("FullClass");
        node.setAttributes("x:int");
        node.setMethods("+getX():int");

        renderer.draw(node, context);
        verify(context,
                times(2)).strokeLine(anyInt(),
                anyInt(), anyInt(), anyInt(),
                any(Color.class),
                eq(LineStyle.SOLID));
    }

    @Test
    @DisplayName("Should kill draw method mutation: replaced operators inside if(attributeBoxHeight > 0)")
    void shouldKillDrawMethodAttributeBoxHeightMutation() {
        node.setName("FullClass");
        node.setAttributes("x:int");
        node.setMethods("+getX():int");

        renderer.draw(node, context);

        ArgumentCaptor<Integer> yCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(context, atLeast(2)).strokeLine(
                anyInt(),
                yCaptor.capture(),
                anyInt(),
                yCaptor.capture(),
                any(),
                eq(LineStyle.SOLID));

        List<Integer> ys = yCaptor.getAllValues();

        int firstLineY = ys.get(0);
        int secondLineY = ys.get(2);

        assertTrue(
                firstLineY > 0, "First separator should be below top of node");
        assertTrue(
                secondLineY > firstLineY, "Second separator must be below first (kills +→- mutation)");
    }

    @Test
    @DisplayName("Should kill draw method mutation: replaced operators inside if(methodBoxHeight > 0)")
    void shouldKillDrawMethodMethodBoxHeightMutation() {
        node.setName("FullClass");
        node.setAttributes("");
        node.setMethods("+getX():int");

        renderer.draw(node, context);

        ArgumentCaptor<Integer> yCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(context, atLeast(1)).strokeLine(
                anyInt(),
                yCaptor.capture(),
                anyInt(),
                yCaptor.capture(),
                any(),
                eq(LineStyle.SOLID));

        List<Integer> ys = yCaptor.getAllValues();

        int firstLineY = ys.getFirst();

        assertTrue(firstLineY > 0, "First separator should be below top of node");
    }

    @Test
    @DisplayName("Should kill draw name method mutation: kills +- mutation")
    void shouldKillDrawNameReplaceSubtractionWithAdditionMutation() {
        node.setName("Top\nMiddle\nBottom");
        node.setAttributes("");
        node.setMethods("");

        renderer.draw(node, context);

        ArgumentCaptor<Rectangle> captor = ArgumentCaptor.forClass(Rectangle.class);
        verify(context, atLeast(3))
                .drawText(anyString(), captor.capture(), any(), any(), any(), any());

        List<Integer> ys = captor.getAllValues().stream().map(Rectangle::y).toList();

        int firstY = ys.getFirst();
        int lastY = ys.getLast();
        int centerY = renderer.getBounds(node).center().y();

        assertTrue(firstY < centerY, "First line should start above vertical center");
        assertTrue(lastY > centerY, "Last line should end below center");
    }

    @Test
    @DisplayName("Should kill draw name method mutation: kills */ mutation")
    void shouldKillDrawNameReplaceMultiplicationWithDivisionMutation() {
        node.setName("One\nTwo\nThree\nFour");
        node.setAttributes("");
        node.setMethods("");

        renderer.draw(node, context);

        ArgumentCaptor<Rectangle> captor = ArgumentCaptor.forClass(Rectangle.class);
        verify(context, atLeast(4))
                .drawText(anyString(), captor.capture(), any(), any(), any(), any());

        List<Integer> ys = captor.getAllValues().stream().map(Rectangle::y).toList();

        List<Integer> gaps = new ArrayList<>();
        for (int i = 1; i < ys.size(); i++) {
            gaps.add(ys.get(i) - ys.get(i - 1));
        }

        int avgGap = (int) gaps.stream().mapToInt(Integer::intValue).average().orElse(0);
        for (int gap : gaps) {
            assertTrue(Math.abs(gap - avgGap) < avgGap / 3,
                    "Each name line should have consistent spacing");
        }
    }

    @Test
    @DisplayName("Should kill draw name method mutation: kills /* mutation")
    void shouldKillDrawNameReplaceDivisionWithMultiplicationMutation() {
        node.setName("Alpha\nBeta\nGamma\nDelta\nEpsilon");
        node.setAttributes("");
        node.setMethods("");

        renderer.draw(node, context);

        ArgumentCaptor<Rectangle> captor = ArgumentCaptor.forClass(Rectangle.class);
        verify(context, atLeast(5))
                .drawText(anyString(), captor.capture(), any(), any(), any(), any());

        List<Rectangle> rects = captor.getAllValues();

        Rectangle first = rects.getFirst();
        Rectangle last = rects.getLast();
        int blockCenter = (first.y() + last.y()) / 2;
        int nodeCenter = renderer.getBounds(node).center().y();

        assertTrue(Math.abs(blockCenter - nodeCenter) < renderer.getBounds(node).height() / 4,
                "Text block should be centered within node bounds");
    }

    @Test
    @DisplayName("Should kill draw name method mutation: if(containsMarkup(line, ITALIC_MARKUP) false and removed call")
    void shouldKillDrawNameContainsMarkupMutations() {
        node.setName("/ItalicName/");
        node.setAttributes("");
        node.setMethods("foo():void");

        renderer.draw(node, context);

        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        verify(context, atLeastOnce()).drawText(
                textCaptor.capture(),
                any(Rectangle.class),
                any(),
                any(),
                any(),
                any()
        );

        List<String> drawnTexts = textCaptor.getAllValues();
        assertTrue(
                drawnTexts.stream().anyMatch(s -> s.equals("ItalicName")),
                "Italic markup should be stripped before drawing"
        );
        assertTrue(
                drawnTexts.stream().noneMatch(s -> s.contains("/")),
                "Italic markup / should not appear in drawn text"
        );
    }

    @Test
    @DisplayName("Should kill draw attribute method mutation: if(attribute, UNDERLINE_MARKUP) false and removed call")
    void shouldKillDrawAttributeContainsMarkupMutations() {
        node.setName("underline");
        node.setAttributes("_underline_");
        node.setMethods("foo():void");

        renderer.draw(node, context);

        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);

        verify(context, atLeast(3)).drawText(
                textCaptor.capture(),
                any(Rectangle.class),
                any(),
                any(),
                any(),
                any()
        );
        List<String> drawnTexts = textCaptor.getAllValues();

        assertTrue(
                drawnTexts.stream().anyMatch(s -> s.equals("underline")),
                "Underline markup should be stripped before drawing"
        );
        assertTrue(
                drawnTexts.stream().noneMatch(s -> s.contains("_")),
                "underline markup _ should not appear in drawn text"
        );
    }
}
