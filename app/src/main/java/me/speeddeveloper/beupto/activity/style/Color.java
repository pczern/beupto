package me.speeddeveloper.beupto.activity.style;

/**
 * Created by phili on 7/19/2016.
 */
public class Color{
    String name;
    int color;
    String identifier;
    public Color(String name, String identifier, int color) {
        this.name = name;
        this.color = color;
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }



}
