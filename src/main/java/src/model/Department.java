package src.model;

/**
 * Created by caoshibin on 2018/1/26.
 */
public class Department implements Comparable {
    private String name;
    private String parentid;
    private String parentName;
    private int order;
    private String id;
    public Department(String name, String parentid,String parentName, int order, String id){
        this.name=name;
        this.parentid=parentid;
        this.order=order;
        this.id=id;
        this.parentName=parentName;
    }
    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    @Override
    public String toString() {
        return "Department{" +
                "name='" + name + '\'' +
                ", parentid='" + parentid + '\'' +
                ", parentName='" + parentName + '\'' +
                ", order='" + order + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentid() {
        return parentid;
    }

    public void setParentid(String parentid) {
        this.parentid = parentid;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int compareTo(Object o) {
        Department department=(Department) o;
        if(this.order<department.getOrder()){
            return -1;
        }else if(this.order>department.getOrder()){
            return 1;
        }else{
            return Integer.valueOf(this.id)-Integer.valueOf(department.getId());
        }

    }
}
