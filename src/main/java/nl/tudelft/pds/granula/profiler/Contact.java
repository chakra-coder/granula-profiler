package nl.tudelft.pds.granula.profiler;

/**
 * Created by wlngai on 1/10/16.
 */
public class Contact  {

    private int id;
    private String name;
    private String phone;
    private String address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // getters and setters

    public static Contact createContact() {
        Contact contact = new Contact();
        contact.setId(12345);
        contact.setName("John");
        contact.setPhone("0733434435");
        contact.setAddress("Sunflower Street, No. 6");
        return contact;
    }

}
