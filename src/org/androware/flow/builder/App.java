package org.androware.flow.builder;

import org.androware.flow.base.FlowBase;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jkirkley on 8/16/16.
 */
public class App {

    public static void main(String a[]) {
        try {

            List<ObjectReadListener> listeners = new ArrayList<>();
            listeners.add(new LinkObjectReadListener());
            JsonObjectReader jsonObjectReader = new JsonObjectReader("/home/jkirkley/tmp/tflow.js", FlowBase.class, null, listeners);

            FlowBase flow = (FlowBase)jsonObjectReader.read();
            System.out.println("flow: " + flow);

            JFrame jFrame = new JFrame("yah");
            MainForm mainForm = new MainForm();

            //mainForm.clearTree();
            jFrame.setContentPane(mainForm.getRootPanel());


            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            jFrame.setLocation(1700, 100);

            mainForm.buildFlowTree(flow);

            jFrame.pack();
            jFrame.setSize(1200, 800);
            jFrame.setVisible(true);

        } catch (ObjectReadException e) {

        }
    }
}
