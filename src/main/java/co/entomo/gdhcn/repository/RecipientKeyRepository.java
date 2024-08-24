package co.entomo.gdhcn.repository;

import co.entomo.gdhcn.entity.QrCode;
import co.entomo.gdhcn.entity.RecipientKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 *
 *  @author Sergio Penafiel
 *  @organization Create SpA
 * Repository interface for managing {@link RecipientKey} entities.
 * Extends {@link JpaRepository} to provide basic CRUD operations.
 */
public interface RecipientKeyRepository extends JpaRepository<RecipientKey, String> {
    /**
     * Finds a {@link RecipientKey} entity by its ID and jsonId.
     *
     * @param id     the ID of the {@link RecipientKey}.
     * @param jsonId the jsonId associated with the {@link RecipientKey}.
     * @return an {@link Optional} containing the found {@link RecipientKey}, or {@code Optional.empty()} if no matching entity is found.
     */
    Optional<RecipientKey> findByIdAndJsonId(String id, String jsonId);
}
