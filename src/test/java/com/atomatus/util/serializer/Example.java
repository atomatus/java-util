package com.atomatus.util.serializer;

import com.atomatus.util.DecimalHelper;
import com.atomatus.util.security.KeyGenerator;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@SuppressWarnings({"unused", "UnnecessaryLocalVariable"})
public class Example implements Serializable {

    private long id;

    private String name;

    private BigDecimal value;

    private Date date;

    @XStreamImplicit(itemFieldName = "others")
    private List<Example> others;

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public List<Example> getOthers() {
        return others;
    }

    public void setOthers(List<Example> others) {
        this.others = others;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private boolean equalsList(List<Example> l0, List<Example> l1) {
        if(l0 == l1) return true;
        else if(l0 == null || l1 == null || (l0.size() != l1.size())) return false;
        else {
            for(Example e : l0) {
                if(!l1.contains(e)) {
//                    System.err.printf("Element %1$s is not present on other list!", e);
                    return false;
                }
            }
            return true;
        }
    }

    private boolean equals(Object ob0, Object ob1, String name) {
        boolean r = Objects.equals(ob0, ob1);
//        if(!r){
//            System.err.printf("Example parameter %1$s are not equals: %2$s != %3$s", name, ob0, ob1);
//        }
        return r;
    }

    private boolean equals(Date d0, Date d1, String name) {
        boolean r;
        if(d0 == null && d1 == null) {
            return true;
        } else if(d0 == null || d1 == null) {
            r = false;
        } else {

            Calendar c0 = Calendar.getInstance();
            Calendar c1 = Calendar.getInstance();

            c0.setTime(d0);
            c1.setTime(d1);
            r = c0.get(Calendar.DATE) == c1.get(Calendar.DATE) &&
                    c0.get(Calendar.MONTH) == c1.get(Calendar.MONTH) &&
                    c0.get(Calendar.YEAR) == c1.get(Calendar.YEAR) &&
                    c0.get(Calendar.HOUR) == c1.get(Calendar.HOUR) &&
                    c0.get(Calendar.MINUTE) == c1.get(Calendar.MINUTE) &&
                    c0.get(Calendar.SECOND) == c1.get(Calendar.SECOND);
        }

//        if(!r){
//            System.err.printf("Example parameter %1$s are not equals: %2$s != %3$s", name, d0, d1);
//        }

        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        else if (o == null) {
//            System.err.println("Object is null!");
            return false;
        } else if (getClass() != o.getClass()) {
//            System.err.printf("%1$s is not a Example class!\n", o.getClass().getSimpleName());
            return false;
        }

        Example other = (Example) o;
        return  equals(id, other.id, "id") &&
                equals(name, other.name, "name") &&
                equals(value, other.value, "value") &&
                equals(date, other.date, "date") &&
                equalsList(others, other.others);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "Example{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", date=" + date +
                ", others=" + others +
                '}';
    }

    private static Example buildExample() {
        Example ex = new Example();
        ex.setDate(new Date());
        ex.setId(Long.parseLong(KeyGenerator.generateRandomKey(10)));
        ex.setName(KeyGenerator.generateRandomKeyHex(10));
        ex.setValue(DecimalHelper.toBigDecimal(new Random().nextDouble()).setScale(2, RoundingMode.HALF_EVEN));
        return ex;
    }

    public static Example getExample() {
        Example ex = buildExample();
        ex.setOthers(new ArrayList<>());
        ex.getOthers().add(buildExample());
        ex.getOthers().add(buildExample());
        return ex;
    }

}
