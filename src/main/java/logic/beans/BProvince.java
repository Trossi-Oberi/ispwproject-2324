package logic.beans;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BProvince {
    private String name;

    private ArrayList<String> provinceList = new ArrayList<String>();

    public BProvince() {

    }

    public void setProvince(String prov){
        this.name = prov;
    }

    public String getProvince(){
        return this.name;
    }

    public void setProvincesList(ArrayList<String> provinceL){
        this.provinceList = provinceL;
    }

    public ArrayList<String> getProvincesList(){
        return this.provinceList;
    }

}