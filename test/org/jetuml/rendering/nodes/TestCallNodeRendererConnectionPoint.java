package org.jetuml.rendering.nodes;

import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Point;
import org.jetuml.rendering.SequenceDiagramRenderer;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Tests the connection point returned by CallNodeRenderer:
 * - EAST -> (maxX, y)
 * - WEST -> (x, y)
 * The x-position depends on lifeline centerX and nesting depth.
 */
public class TestCallNodeRendererConnectionPoint {

    /**
     * Small fixture to prepare common mocks.
     * We mock:
     * - parent renderer
     * - call node and its lifeline (parent)
     * - layout values: y, maxY, nestingDepth
     * We do NOT set x here because it uses a static method which we stub per test.
     */
    private static class Fx {
        final SequenceDiagramRenderer parent = mock(SequenceDiagramRenderer.class);
        final CallNode call = mock(CallNode.class);
        final ImplicitParameterNode lifeline = mock(ImplicitParameterNode.class);
        final CallNodeRenderer renderer = new CallNodeRenderer(parent);

        Fx(int y, int height, int nestingDepth) {
            when(call.getParent()).thenReturn(lifeline);
            when(parent.getNestingDepth(call)).thenReturn(nestingDepth);
            when(parent.getY(call)).thenReturn(y);
            when(parent.getMaxY(call)).thenReturn(y + height);
        }
    }

    @Test
    void eastDirectionReturnsMaxXAndY() {
        // GIVEN
        int centerX = 110, y = 200, height = 30, depth = 1;
        Fx fx = new Fx(y, height, depth);

        // Stub static method that return the lifeline center X
        try (MockedStatic<SequenceDiagramRenderer> st = mockStatic(SequenceDiagramRenderer.class)) {
            st.when(() -> SequenceDiagramRenderer.getCenterXCoordinate(fx.lifeline)).thenReturn(centerX);

            // WHEN
            Point p = fx.renderer.getConnectionPoint(fx.call, Direction.EAST);

            // THEN
            // WIDTH = 16,
            // x = centerX - 10 + 10 * depth = 110 => maxX = 110 + 16 = 126
            assertEquals(126, p.x());
            assertEquals(y, p.y());
        }
    }

    @Test
    void westDirectionReturnsXAndY() {
        // GIVEN
        int centerX = 110, y = 200, height = 30, depth = 1;
        Fx fx = new Fx(y, height, depth);

        // Stub static method again for this test
        try (MockedStatic<SequenceDiagramRenderer> st = mockStatic(SequenceDiagramRenderer.class)) {
            st.when(() -> SequenceDiagramRenderer.getCenterXCoordinate(fx.lifeline)).thenReturn(centerX);

            // WHEN
            Point p = fx.renderer.getConnectionPoint(fx.call, Direction.WEST);

            // THEN
            // x = centerX - 10 + 10 * depth = 110
            assertEquals(110, p.x());
            assertEquals(y, p.y());
        }
    }
}
