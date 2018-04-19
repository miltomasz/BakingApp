package com.plumya.bakingapp.data.model;

import java.io.Serializable;

/**
 * Created by miltomasz on 13/04/18.
 */

public class Step implements Serializable {
    public long id;
    public String shortDescription;
    public String description;
    public String videoURL;
    public String thumbnailURL;
}
