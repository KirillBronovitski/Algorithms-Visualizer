package visualizer;

import visualizer.gui.MainFrame;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class ApplicationRunner {
    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(MainFrame::new);
        } catch (InvocationTargetException | InterruptedException e) {
            System.out.println("Something evil is brewing");
        }
    }
}
