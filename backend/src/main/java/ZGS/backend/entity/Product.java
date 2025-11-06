package ZGS.backend.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 5000)
    private String description;

    private String imageUrl;

    private int price;

    @Builder.Default
    private Integer stock = 0; // 庫存數量

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private List<Review> reviews;
}