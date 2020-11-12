package models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrder {
    private long id;
    private long petId;
    private int quantity;
    private Date shipDate;
    private String status;
    private boolean complete;
}
