package be.kdg.cluedobackend.model.notebook;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table
public class NoteLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noteLineId;
    private String card;
    private boolean crossed = false;
    @Column
    @Enumerated
    @ElementCollection(targetClass = NotationSymbol.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<NotationSymbol> columns;

    public List<NotationSymbol> getColumns() {
        return columns;
    }

    public void setColumns(List<NotationSymbol> columns) {
        this.columns = columns;
    }
}
