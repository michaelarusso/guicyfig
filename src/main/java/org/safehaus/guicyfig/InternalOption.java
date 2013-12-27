package org.safehaus.guicyfig;


import com.google.common.base.Preconditions;
import com.google.common.hash.HashCode;
import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicDoubleProperty;
import com.netflix.config.DynamicFloatProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.PropertyWrapper;


/**
 * A module's configuration options.
 */
class InternalOption<T extends PropertyWrapper> implements ConfigOption {
    private final String key;
    private final T property;
    private Object currentValue;
    private String bypassValue;
    private ConcurrentMapConfiguration mapConfiguration;


    InternalOption( String key, T property ) {
        Preconditions.checkNotNull( key, "key cannot be null" );
        Preconditions.checkNotNull( property, "property cannot be null" );

        this.key = key;
        this.property = property;
        this.currentValue = property.getValue();
    }


    @Override
    public String key() {
        return key;
    }


    void setCurrentValue( Object value ) {
        currentValue = value;
    }


    void setOverrideMapConfiguration( ConcurrentMapConfiguration mapConfiguration ) {
        this.mapConfiguration = mapConfiguration;
    }


    @Override
    public void setOverride( String override ) {
        mapConfiguration.setProperty( key, override );
    }


    Object getCurrentValue() {
        return currentValue;
    }


    Object getNewPropertyValue() {
        return property.getValue();
    }


    @Override
    public void setBypass( String value ) {
        this.bypassValue = value;
    }


    boolean isBypassed() {
        return bypassValue != null;
    }


    public Object getBypass() {
        return convertValue( bypassValue );
    }


    Object convertValue( String value ) {
        if ( property instanceof DynamicStringProperty ) {
            return value;
        }

        Preconditions.checkNotNull( value, "the value cannot be null if we're going to be parsing it" );

        if ( property instanceof DynamicIntProperty ) {
            return Integer.parseInt( value );
        }

        if ( property instanceof DynamicBooleanProperty ) {
            return Boolean.parseBoolean( value );
        }

        if ( property instanceof DynamicLongProperty ) {
            return Long.parseLong( value );
        }

        if ( property instanceof DynamicFloatProperty ) {
            return Float.parseFloat( value );
        }

        if ( property instanceof DynamicDoubleProperty ) {
            return Double.parseDouble( value );
        }

        throw new IllegalArgumentException( "Don't know how to convert the property: " + property.toString() );
    }


    @Override
    public Object value() {
        return currentValue;
    }


    @Override
    public int hashCode() {
        return HashCode.fromString( key ).hashCode();
    }
}
