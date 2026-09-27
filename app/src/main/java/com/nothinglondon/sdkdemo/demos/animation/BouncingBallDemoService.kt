package com.nothinglondon.sdkdemo.demos.animation

import android.content.Context
import com.nothing.ketchum.GlyphMatrixManager
import com.nothinglondon.sdkdemo.demos.GlyphMatrixService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

class boundary(val width: Int, val height: Int){
    var mygrid = Array(HEIGHT * WIDTH) { 0 }
    fun create_wall_grid(){
        for (i in 0..<WIDTH){
           for (j in  0..<HEIGHT){
               if (((i > 17) or (i < 7)) or ((j > 19) or (j < 5))){
                   mygrid[i * WIDTH + j] = 255 // max brightness
                   //setting 2d array value in 1d array
               }
           }
        }

    }
    private companion object {
        private const val WIDTH = 25
        private const val HEIGHT = 25
        private const val HALF_HEIGHT = HEIGHT.toDouble() / 2
        private const val MID_POINT = HEIGHT / 2
        private const val ANGLE_PER_PIXEL_DEGREES = 360.0 / WIDTH
    }
}

class ball(var xspeed: Int, var yspeed: Int){
    var xpos : Int = 12
    var ypos : Int = 12
    var isRunning : Boolean = false
    var wall = boundary(8,9)

    fun check_bounce(nextframevalues: Array<Int>): Array<Int>{
        var corrected_next_frame = nextframevalues
        if (nextframevalues[0] > 17){
            corrected_next_frame[0] = 17
            xspeed = -xspeed
        }
        else if (nextframevalues[0] < 7){
            corrected_next_frame[0] = 7
            xspeed = -xspeed
        }
        if (nextframevalues[1] > 19){
            corrected_next_frame[1] = 19
            yspeed = -yspeed
        }
        else if (nextframevalues[1] < 5){
            corrected_next_frame[1] = 5
            yspeed = -yspeed
        }
        return corrected_next_frame
    }
    
    fun get_next_pos(): Array<Int>{
        val newxpos = xpos + xspeed
        val newypos = ypos + yspeed
        return arrayOf(newxpos, newypos)
    }
    
    
    fun get_next_frame(){
        var nextframevalues = get_next_pos()
        nextframevalues = check_bounce(nextframevalues)
        
        xpos = nextframevalues[0]
        ypos = nextframevalues[1]
    }
    
    
}


class BouncingBallToy : GlyphMatrixService("Bouncing-Ball") {
    private val backgroundScope = CoroutineScope(Dispatchers.IO)
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private var frame = 0
    private var newball = ball(2,1)

    override fun onTouchPointLongPress() {
        println("change something")
    }



    override fun performOnServiceConnected(
        context: Context,
        glyphMatrixManager: GlyphMatrixManager
    ) {
        backgroundScope.launch {
            while (isActive) {
                newball.wall.create_wall_grid()
                val array = generateNextAnimationFrame()
                uiScope.launch {
                    glyphMatrixManager.setMatrixFrame(array)
                }
                // wait a bit
                delay(30)
                // next frame
                frame++
                if (frame >= WIDTH) {
                    frame = 0
                }
            }
        }
    }

    override fun performOnServiceDisconnected(context: Context) {
        backgroundScope.cancel()
    }

    private fun create_ball_grid() : Array<Int>{
        var ball_grid = Array(HEIGHT * WIDTH) { 0 }
        //get ball position as first point
        ball_grid[newball.xpos * WIDTH + newball.ypos] = 255
        //point behind x
        var lagginxgpos = newball.xpos - (newball.xspeed / newball.xspeed)
        //point behind y
        var laggingypos = newball.ypos - (newball.yspeed/newball.yspeed)
        ball_grid[newball.xpos * WIDTH + laggingypos] = 255
        ball_grid[lagginxgpos * WIDTH + newball.ypos] = 255
        //point diagonal behind both
        ball_grid[lagginxgpos * WIDTH + laggingypos] = 255
        return ball_grid
    }

    private fun generateNextAnimationFrame(): IntArray {
        // Get ball position
        newball.get_next_frame()
        val ball_x_position = newball.xpos
        val ball_y_position = newball.ypos

        val ball_drawing = create_ball_grid()
        val border_drawing = newball.wall.mygrid


        // Start with every LED turned off.
        var grid = Array(HEIGHT * WIDTH) { 0 }

        grid.forEachIndexed{ index, value1 ->
            grid[index] = maxOf(ball_drawing[index], border_drawing[index])
        }


        return grid.toIntArray()
    }

    private companion object {
        private const val WIDTH = 25
        private const val HEIGHT = 25
        private const val HALF_HEIGHT = HEIGHT.toDouble() / 2
        private const val MID_POINT = HEIGHT / 2
        private const val ANGLE_PER_PIXEL_DEGREES = 360.0 / WIDTH
    }

}
