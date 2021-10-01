/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lemonade

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val LEMONADE_STATE = "LEMONADE_STATE"
    private val LEMON_SIZE = "LEMON_SIZE"
    private val SQUEEZE_COUNT = "SQUEEZE_COUNT"



    // SELECT - "pick lemon" state
    private val SELECT = "select"

    // SQUEEZE - "squeeze lemon" state
    private val SQUEEZE = "squeeze"

    // DRINK - "drink lemonade" state
    private val DRINK = "drink"

    // RESTART - lemonade has be drunk and the glass is empty
    private val RESTART = "restart"

    // Default state
    private var lemonadeState = "select"

    // Filter out logs by using a tag (DM, etc)
    // println("DM: lemonadeState is: ${lemonadeState}")



    // Default
    private var lemonSize = -1

    // Default
    private var squeezeCount = -1

    private var lemonTree = LemonTree()

    // ? allows for the view to be null/not null
    // No ? means it would need to be defined during its declaration
    // All views will start as null as MainActivity is automatically created
    // setContentView in onCreate actually sets the view (which has nothing)
    private var lemonImage: ImageView? = null
    private var lemonText: TextView? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            lemonadeState = savedInstanceState.getString(LEMONADE_STATE, "select")
            lemonSize = savedInstanceState.getInt(LEMON_SIZE, -1)
            squeezeCount = savedInstanceState.getInt(SQUEEZE_COUNT, -1)
        }

        // The layout has been set with a blank view; Find the views by their id
        lemonImage = findViewById(R.id.image_lemon_state)
        lemonText = findViewById(R.id.text_action)

        setViewElements()

        // !! says it promises that it's not null - forces it to run; !! will crash if the view is null
        // If not sure it's null, use ?; ? will prevent crashing if null but will run/not allow you to do anything
        lemonImage!!.setOnClickListener {
            clickLemonImage()
        }

        lemonImage!!.setOnLongClickListener {
            showSnackbar()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(LEMONADE_STATE, lemonadeState)
        outState.putInt(LEMON_SIZE, lemonSize)
        outState.putInt(SQUEEZE_COUNT, squeezeCount)
        super.onSaveInstanceState(outState)
    }

    private fun clickLemonImage() {
        // update the state to the next stage of 'squeeze'
        when (lemonadeState) {
            "squeeze" -> {

                if (lemonSize > 0) {
                    lemonSize -= 1
                } else {
                    lemonadeState = "drink"
                    lemonSize = -1
                }

                squeezeCount += 1
            }
            "drink" -> {
                lemonadeState = "restart"
            }
            "restart" -> {
                lemonadeState = "select"
            }
            else -> {
                lemonadeState = "squeeze"
                lemonSize = lemonTree.pick()
                squeezeCount = 0
            }
        }

        setViewElements()
    }

    private fun setViewElements() {
        when (lemonadeState) {
            "squeeze" -> {
                lemonImage!!.setImageResource(R.drawable.lemon_squeeze)
                lemonText!!.text = resources.getString(R.string.lemon_squeeze)
            }
            "drink" -> {
                lemonImage!!.setImageResource(R.drawable.lemon_drink)
                lemonText!!.text = resources.getString(R.string.lemon_drink)
            }
            "restart" -> {
                lemonImage!!.setImageResource(R.drawable.lemon_restart)
                lemonText!!.text = resources.getString(R.string.lemon_empty_glass)
            }
            else -> {
                lemonImage!!.setImageResource(R.drawable.lemon_tree)
                lemonText!!.text = resources.getString(R.string.lemon_select)
            }
        }
    }

    private fun showSnackbar(): Boolean {
        if (lemonadeState != SQUEEZE) {
            return false
        }
        val squeezeText = getString(R.string.squeeze_count, squeezeCount)
        Snackbar.make(
            findViewById(R.id.constraint_Layout),
            squeezeText,
            Snackbar.LENGTH_SHORT
        ).show()
        return true
    }
}