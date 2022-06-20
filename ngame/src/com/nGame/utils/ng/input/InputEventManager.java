package com.nGame.utils.ng.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;
import com.nGame.utils.ng.Log;

import java.util.HashMap;

/**
 * Created by n0iz on 01.02.2015.
 */
public class InputEventManager extends InputListener implements ControllerListener{

    private final boolean hasGamePad;
    private GamePads gamepads;
    private HashMap<String,InputResponceEvent> events;

    private HashMap<Integer,String> keyUpBinding = new HashMap<Integer,String>();
    private HashMap<Integer,String> keyDownBinding = new HashMap<Integer,String>();
    private Array<HashMap<Integer,String>> gamePadBindings = new Array<HashMap<Integer, String>>();

    public InputEventManager(){
        gamepads= GamePads.I;
        hasGamePad=gamepads.gamepadCount>0;
        if(hasGamePad){
            //do gamepad init stuff
            for(GamePads.GamePad pads : gamepads.controllerMap){
                gamePadBindings.add(new HashMap<Integer, String>());
            }


        }


        events = new HashMap<String, InputResponceEvent>();


    }


/**
 * Activate GamePad Mapping
 */
    public void activateGamePad(){
        Controllers.addListener(this);
    }

    /**
     * Deactivate GamePad Mapping
     */
    public void deactivateGamePad(){
        Controllers.removeListener(this);
    }


    /**
     * Add an InputEvent
     * To bind first key, use first bind function like {@link #bindKeyUp(int, String)}
     * @param event The InputEvent
     */
    public void addInputResponseEvent(InputResponceEvent event) {
        //remove existing key,event pair

        if(events.containsKey(event.identifier)){
            Log.d("remove existing event \"" + event +"\" before re add");
            events.remove(events.get(event.identifier));
        }

        //put event in map
        events.put(event.identifier,event);
    }

    public void removeEvent(InputResponceEvent event){
        events.remove(event);
    }

    public void removeEvent(String identifier){
        events.remove(events.get(identifier));

    }


    void fireEvent(String eventIdentifier, float value) {
        if(events.containsKey(eventIdentifier)){
            events.get(eventIdentifier).onAction(value);
        } else {
            Log.e("Event unknown: " + eventIdentifier);
        }
    }




    /**
     * Bind Default Inputs to {@link InputResponceEvent}s
     *
     * @param keycode {@link com.badlogic.gdx.Input.Keys}
     * @param eventIdentifier
     * @throws Exception
     */
    public void bindKeyUp(int keycode, String eventIdentifier){
        if(events.containsKey(eventIdentifier)){
            if(keyUpBinding.containsKey(keycode)){
                Log.i("Key " + keycode + " will be remapped to " + eventIdentifier);
                keyUpBinding.remove(keyUpBinding.get(keycode));
            } else {
                Log.i("Key " + keycode + " mapped to " + eventIdentifier);
            }
            keyUpBinding.put(keycode,eventIdentifier);
        } else {
            Log.e("Event unknown: " + eventIdentifier);
        }
    }

    public void bindKeyDown(int keycode, String eventIdentifier){
        if(events.containsKey(eventIdentifier)){
            if(keyDownBinding.containsKey(keycode)){
                Log.i("Key " + keycode + " will be remapped to " + eventIdentifier);
                keyDownBinding.remove(keyDownBinding.get(keycode));
            } else {
                Log.i("Key " + keycode + " mapped to " + eventIdentifier);
            }
            keyDownBinding.put(keycode,eventIdentifier);
        } else {
            Log.e("Event unknown: " + eventIdentifier + " key "+keycode+" not bound");
        }
    }


    /**
     * Bind GamePad Button to {@link InputResponceEvent}
     * @param gamepadID The GamePad ID (@link GamePads#)
     * @param buttonCode The buttoncode for the gamepad button
     *                (e.g. {@link com.nGame.utils.ng.input.XboxController})
     * @param eventIdentifier The identifier of the {@link InputResponceEvent}
     * @throws Exception
     */
    public void bindGamePadButton(int gamepadID, int buttonCode, String eventIdentifier) throws Exception {
        if(!gamepads.existID(gamepadID)){
            Log.e("Unknown GamePadID: " + gamepadID);
            return;
        }
        //Get gamepad binding map
        HashMap<Integer,String> bindingMap = gamePadBindings.get(gamepadID);

        if(events.containsKey(eventIdentifier)){
            if(bindingMap.containsKey(buttonCode)){
                Log.i("Button " + buttonCode + " will be remapped to " + eventIdentifier);
                bindingMap.remove(bindingMap.get(buttonCode));
            } else {
                Log.i("Button " + buttonCode + " mapped to " + eventIdentifier);
            }
            bindingMap.put(buttonCode,eventIdentifier);
        } else {
            Log.e("Event unknown: " + eventIdentifier);
            throw new Exception("event identifier unknown");
        }
    }

    //////////////////////////////
    // Keyboad
    /////////////////////////////


    @Override
    public boolean keyDown(InputEvent event, int keycode) {
        if(keyDownBinding.containsKey(keycode)){
            fireEvent(keyDownBinding.get(keycode), 1);
            return true;
        }
        return super.keyDown(event, keycode);
    }

    @Override
    public boolean keyUp(InputEvent event, int keycode) {
        if(keyUpBinding.containsKey(keycode)){
            fireEvent(keyUpBinding.get(keycode), 1);
            return true;
        }
        return super.keyUp(event, keycode);
    }

    /////////////////////////
    // GamePad
    ///////////////////////
    private String getControllerIdentifier(Controller controller){
        /*GamePads.GamePad pad = gamepads.getGamePad(controller);
        if(pad==null) {
            Log.d("Controller unknown : " + controller.getName());
            return controller.getName();
        }
        return pad.padID+"|"+pad.padType+"|"+controller.getName();*/
        return controller.getName();
    }

    @Override
    public void connected(Controller controller) {
        Log.d("Controller connected: " +getControllerIdentifier(controller));
    }

    @Override
    public void disconnected(Controller controller) {
        Log.d("Controller disconnected: " +getControllerIdentifier(controller));
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        Log.d("Button down [" +getControllerIdentifier(controller) + "]: " + buttonCode);

        try {
            HashMap<Integer,String> map = gamePadBindings.get(gamepads.getGamePadID(controller));

        if(map.containsKey(buttonCode)){
            fireEvent(map.get(buttonCode), 1);
            return true;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        Log.d("Button up [" +getControllerIdentifier(controller) + "]: " + buttonCode);
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        if(value>.2f)
            Log.d("axis moved [" +getControllerIdentifier(controller) +  "]: " + axisCode +" " + value);
        return false;
    }

}
