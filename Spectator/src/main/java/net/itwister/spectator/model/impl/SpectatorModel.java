package net.itwister.spectator.model.impl;

import android.content.Context;

import bindui.InjectService;

public class SpectatorModel {

    protected Context getContext(){
        return InjectService.getInstance(Context.class);
    }
}