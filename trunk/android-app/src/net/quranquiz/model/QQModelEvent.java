/**
 * 
 */
package net.quranquiz.model;

import java.util.EventListener;
import java.util.EventObject;

import mirror.javax.swing.event.EventListenerList;

/**
 * A simple class that defines all events dispatched by the model.
 * UI classes should implement the listener and handles those events.
 * <ul>
 *  <li> UI Classes should implement QQModelEvents.Listener
 *  <li> Model can dispatch events using dispatchEvent 
 * </ul>
 * 
 * @author TELDEEB
 * @author <a href="http://www.andygup.net/using-custom-events-in-android-apps">andygup</a>  
 */
public class QQModelEvent extends EventObject{
	 
    private static final long serialVersionUID = 1L;
 
    public QQModelEvent(Object source){
        super(source);
    }
	
	public interface Listener extends EventListener{
	    /**
	     * Indicates there has been a UI change received from Model.
	     * @param event
	     * @param message
	     */
	    public void onQQUIEvent(QQModelEvent event,String message);
	    /**
	     * Indicates a generic (still undefined/unused) event has come from Model.
	     * @param event
	     * @param message
	     */
	    public void onQQGenericEvent(QQModelEvent event,String message);
	}	 
	
	protected EventListenerList eventListenerList = new EventListenerList();
	 
	/**
	 * Adds the eventListenerList for MapViewController
	 * @param listener
	 */
	public void addEventListener(Listener listener){
	    eventListenerList.add(Listener.class, listener);
	}
	 
	/**
	 * Removes the eventListenerList for MapViewController
	 * @param listener
	 */
	public void removeEventListener(Listener listener){
	    eventListenerList.remove(Listener.class, listener);
	}
	 
	/**
	 * Dispatches QQ Model events
	 * @param event
	 * @param message
	 */
	public void dispatchEvent(QQModelEvent event,String message){
	    Object[] listeners = eventListenerList.getListenerList();
	    Object eventObj = event.getSource();
	    String eventName = eventObj.toString();
	    for(int i=0; i<listeners.length;i+=2){
	        if(listeners[i] == QQModelEvent.Listener.class){
	            if(eventName.contains("UI"))
	                ((Listener) listeners[i+1]).onQQUIEvent(event, message);
	            else 
	                ((Listener) listeners[i+1]).onQQGenericEvent(event, message);
	        }
	    }
	
	}
}
