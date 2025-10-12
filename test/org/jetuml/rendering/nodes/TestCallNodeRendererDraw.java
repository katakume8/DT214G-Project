package org.jetuml.rendering.nodes;

import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.SequenceDiagramRenderer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test how CallNodeRenderer.draw(...) renders:
 * - closed bottom: only one filled rectangle
 * - open bottom: rectangle + 5 lines (3 solid + 2 dotted)
 */
public class TestCallNodeRendererDraw {

    /**
     * Common fixture:
     * - mocks for parent, call, lifeline and graphics context
     * - sets y / maxY and nesting depth
     * x depends on lifeline centerX and depth, so we stub centerX per test.
     */
    private static class Fx {
        final SequenceDiagramRenderer parent = mock(SequenceDiagramRenderer.class);
        final CallNode call = mock(CallNode.class);
        final ImplicitParameterNode lifeline = mock(ImplicitParameterNode.class);
        final CallNodeRenderer renderer = new CallNodeRenderer(parent);
        final RenderingContext ctx = mock(RenderingContext.class);

        Fx(int y, int height, int nestingDepth) {
            when(call.getParent()).thenReturn(lifeline);
            when(parent.getNestingDepth(call)).thenReturn(nestingDepth);
            when(parent.getY(call)).thenReturn(y);
            when(parent.getMaxY(call)).thenReturn(y + height);
        }
    }

    @Test
    void draw_closedBottom_drawsRectangleOnly() {
        // GIVEN: depth=0 (no extra horizontal shift)
        int centerX = 100, y = 200, height = 60, depth = 0;
        Fx fx = new Fx(y, height, depth);
        when(fx.call.isOpenBottom()).thenReturn(false);

        // Stub lifeline center X
        try (MockedStatic<SequenceDiagramRenderer> st = mockStatic(SequenceDiagramRenderer.class)) {
            st.when(() -> SequenceDiagramRenderer.getCenterXCoordinate(fx.lifeline)).thenReturn(centerX);

            // WHEN
            fx.renderer.draw(fx.call, fx.ctx);

            // THEN: capture the rectangle and check its geometry
            ArgumentCaptor<Rectangle> rectCap = ArgumentCaptor.forClass(Rectangle.class);
            verify(fx.ctx, times(1)).drawRectangle(rectCap.capture(), any(), any(), any());
            Rectangle r = rectCap.getValue();

            // x = centerX - 10 + 10 * depth = 100 - 10 = 90
            assertEquals(centerX - 10, r.x());
            assertEquals(y, r.y());
            assertEquals(16, r.width()); // constant WIDTH
            assertEquals(height, r.height());

            // Closed bottom: no extra lines are drawn
            verify(fx.ctx, times(0)).strokeLine(anyInt(), anyInt(), anyInt(), anyInt(), any(), any());
        }
    }

    @Test
    void draw_openBottom_drawsRectangleAndFiveLines() {
        // GIVEN: depth = 1 => x is shifted by + 10
        int centerX = 120, y = 150, height = 50, depth = 1;
        Fx fx = new Fx(y, height, depth);
        when(fx.call.isOpenBottom()).thenReturn(true);

        // Stub lifeline center X
        try (MockedStatic<SequenceDiagramRenderer> st = mockStatic(SequenceDiagramRenderer.class)) {
            st.when(() -> SequenceDiagramRenderer.getCenterXCoordinate(fx.lifeline)).thenReturn(centerX);

            // WHEN
            fx.renderer.draw(fx.call, fx.ctx);

            // Expected geometry
            int x1 = centerX - 10 + 10 * depth; // left
            int x2 = x1 + 16;                   // right (maxX), WIDTH = 16
            int y3 = y + height;                // bottom
            int y2 = y3 - CallNode.CALL_YGAP;   // split between solid and dotted

            // THEN
            verify(fx.ctx, times(1)).drawRectangle(any(Rectangle.class), any(), any(), any());

            // Top and solid side segments
            verify(fx.ctx).strokeLine(eq(x1), eq(y), eq(x2), eq(y), any(), eq(LineStyle.SOLID));
            verify(fx.ctx).strokeLine(eq(x1), eq(y), eq(x1), eq(y2), any(), eq(LineStyle.SOLID));
            verify(fx.ctx).strokeLine(eq(x2), eq(y), eq(x2), eq(y2), any(), eq(LineStyle.SOLID));

            // Dotted lower segments
            verify(fx.ctx).strokeLine(eq(x1), eq(y2), eq(x1), eq(y3), any(), eq(LineStyle.DOTTED));
            verify(fx.ctx).strokeLine(eq(x2), eq(y2), eq(x2), eq(y3), any(), eq(LineStyle.DOTTED));

            // Total five line draws
            verify(fx.ctx, times(5)).strokeLine(anyInt(), anyInt(), anyInt(), anyInt(), any(), any());
        }
    }
}
