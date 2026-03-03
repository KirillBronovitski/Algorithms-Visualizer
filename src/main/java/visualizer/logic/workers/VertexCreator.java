package visualizer.logic.workers;

import javax.swing.*;
import java.util.List;
import java.util.function.BiConsumer;

public class VertexCreator extends SwingWorker<Void, Integer[]> {

    private final int x;
    private final int y;

    private final BiConsumer<Integer, Integer> vertexCreationConsumer;

    public VertexCreator(int x, int y, BiConsumer<Integer, Integer> vertexCreationConsumer) {
        this.x = x;
        this.y = y;
        this.vertexCreationConsumer = vertexCreationConsumer;
    }

    @Override
    protected Void doInBackground() {
        publish(new Integer[]{x, y});
        return null;
    }

    @Override
    protected void process(List<Integer[]> coordinates) {
        for (Integer[] xAndY: coordinates) {
            vertexCreationConsumer.accept(xAndY[0], xAndY[1]);
        }
    }


}
