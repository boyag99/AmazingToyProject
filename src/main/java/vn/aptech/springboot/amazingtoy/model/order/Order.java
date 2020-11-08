package vn.aptech.springboot.amazingtoy.model.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.aptech.springboot.amazingtoy.model.orderdetail.OrderDetail;
import vn.aptech.springboot.amazingtoy.model.shippingaddress.ShippingAddress;
import vn.aptech.springboot.amazingtoy.model.user.BaseEntity;
import vn.aptech.springboot.amazingtoy.model.user.User;

import javax.persistence.*;
import java.util.Collection;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends BaseEntity {

    @Column(name = "amount")
    private int amount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Collection<OrderDetail> orderDetails;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    @ManyToOne
    @JoinColumn(name="shipping_address_id")
    private ShippingAddress shippingAddress;
}
