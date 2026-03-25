package game;

import java.util.List;
import javax.swing.*;

public class GameLoader extends SwingWorker<panel, Object[]> {

    private JFrame parentFrame;
    private LoadingScreen screen;
    public panel loadedPanel;

    public GameLoader(JFrame frame, LoadingScreen screen) {
        this.parentFrame = frame;
        this.screen = screen;
    }

    @Override
    protected panel doInBackground() throws Exception {

        publish(new Object[]{5, "Initializing..."});
        Thread.sleep(120);

        publish(new Object[]{20, "Loading tiles and world..."});
        loadedPanel = new panel(parentFrame);

        publish(new Object[]{75, "Loading sounds..."});
        Thread.sleep(100);

        publish(new Object[]{90, "Preparing game world..."});
        Thread.sleep(80);

        publish(new Object[]{100, "Ready."});
        Thread.sleep(60);

        return loadedPanel;
    }

    // update the LoadingScreen UI
    @Override
    protected void process(List<Object[]> chunks) {
        Object[] latest = chunks.get(chunks.size() - 1);
        screen.setProgress((int) latest[0], (String) latest[1]);
    }

    @Override
    protected void done() {
        screen.onLoadingComplete();
    }
}