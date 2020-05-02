package be.kdg.cluedobackend.repository;

import be.kdg.cluedobackend.model.notebook.NoteBook;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteBookRepository extends JpaRepository<NoteBook, Integer> {
}
