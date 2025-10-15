package org.jetuml.diagram.builder;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestSequenceDiagramBuilderTargetArea {

    // Helper: create a fresh sequence diagram + builder for each test
    private SequenceDiagramBuilder newBuilder() {
        return new SequenceDiagramBuilder(new Diagram(DiagramType.SEQUENCE));
    }

    @Test
    void addCallNode_insideTargetArea_becomesChild() {
        // Arrange
        var b = newBuilder();
        var life = new ImplicitParameterNode();
        b.createAddNodeOperation(life, new Point(100, 20)).execute();

        // Act: add CallNode well below the top rectangle (inside target area)
        var op = b.createAddNodeOperation(new CallNode(), new Point(100, 120));
        assertNotNull(op);
        op.execute();

        // Assert: CallNode became a child of the lifeline
        assertEquals(1, life.getChildren().size());

        // Act (indo)
        op.undo();

        // Assert (after undo): child removed again
        assertEquals(0, life.getChildren().size());
    }

    @Test
    void addCallNode_outsideTargetArea_notChild() {
        // Arrange
        var b = newBuilder();
        var life = new ImplicitParameterNode();
        b.createAddNodeOperation(life, new Point(100, 20)).execute();

        // Act: add CallNode at y=0 (inside the top rectangle -> OUTSIDE target area)
        var op = b.createAddNodeOperation(new CallNode(), new Point(100, 0));
        assertNotNull(op);
        op.execute();

        // Assert: lifeline did NOT receive a child
        assertEquals(0, life.getChildren().size());

        // Act (undo)
        op.undo();

        // Assert (after undo): still no children
        assertEquals(0, life.getChildren().size());
    }
}
