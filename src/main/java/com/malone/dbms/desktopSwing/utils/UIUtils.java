package com.malone.dbms.desktopSwing.utils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UIUtils {
    private ActionListener exceptionListener = null;


    void setExceptionListener(ActionListener exceptionListener) {
        this.exceptionListener = exceptionListener;
    }

    void reportException(String exception) {
        if(exceptionListener != null) {
            ActionEvent evt = new ActionEvent(this, 0, exception);
            exceptionListener.actionPerformed(evt);
        } else {
            JOptionPane.showMessageDialog(null, ""+exception);
            System.err.println(exception);
        }
    }
}