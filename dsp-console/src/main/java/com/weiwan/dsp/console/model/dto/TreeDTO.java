package com.weiwan.dsp.console.model.dto;

/**
 * @author: xiaozhennan
 */
public class TreeDTO {

    private Long id;
    private Long pId;
    private String name;
    private boolean parent;
    private Object obj;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public TreeDTO() {

    }

    public Long getPId() {
        return pId;
    }

    public void setPId(Long pId) {
        this.pId = pId;
    }

    public boolean getIsParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public TreeDTO(Long id, Long pId, String name, boolean parent, Object obj) {
        super();
        this.id = id;
        this.pId = pId;
        this.name = name;
        this.parent = parent;
        this.obj = obj;
    }

}
