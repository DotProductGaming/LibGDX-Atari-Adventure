/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bartsource.adventure;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 *
 * @author bart
 */
class GameSoundManager {

    final Sound mPickupSound;
    final Sound mDropSound;
    final Sound mChompSound;
    final Sound mSlaySound;
    final Sound mWinGoodSound;

    GameSoundManager() {

        mPickupSound = Gdx.audio.newSound(Gdx.files.internal("data/PICKUP.wav"));
        mDropSound = Gdx.audio.newSound(Gdx.files.internal("data/DROP.wav"));
        mChompSound = Gdx.audio.newSound(Gdx.files.internal("data/CHOMP.wav"));
        Sound mDeathSound = Gdx.audio.newSound(Gdx.files.internal("data/DEATH.wav"));
        mSlaySound = Gdx.audio.newSound(Gdx.files.internal("data/SLAY.wav"));
        mWinGoodSound = Gdx.audio.newSound(Gdx.files.internal("data/WIN_GOOD.wav"));
        Sound mWinBadSound = Gdx.audio.newSound(Gdx.files.internal("data/WIN_BAD.wav"));

    }

}


