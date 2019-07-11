package com.pojo;

import com.google.firebase.database.IgnoreExtraProperties;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String body;

    public Post(String uid, String body) {
        this.uid = uid;
        this.body = body;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

