package com.eduhansol.fmlibrary.ui;

import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.PopupMenu;

import java.util.ArrayList;

public class DMPopupMenu extends PopupMenu {

    public DMPopupMenu(Context context, View anchor) {
        super(context, anchor);
    }

    public void setMenuItems(ArrayList<String> list) {

        for (int i = 0; i < list.size(); i++) {
            getMenu().add(Menu.NONE, i, Menu.NONE, list.get(i));
        }
    }

    public void removeMenuItems() {
        getMenu().removeGroup(Menu.NONE);
    }
}
