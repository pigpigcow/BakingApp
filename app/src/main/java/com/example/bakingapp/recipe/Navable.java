package com.example.bakingapp.recipe;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public interface Navable {
    void naveLeft(FloatingActionButton fabLeft, FloatingActionButton fabRight);
    void navRight(FloatingActionButton fabLeft, FloatingActionButton fabRight);
}
