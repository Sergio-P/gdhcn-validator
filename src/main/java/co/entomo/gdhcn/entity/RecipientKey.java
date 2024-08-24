package co.entomo.gdhcn.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

/**
 *  @author Sergio Penafiel
 *  @organization Create SpA
 */
@Data
@Entity
@Table(name = "recipient_key")
public class RecipientKey {

	@Id
	@Column(length = 36)
	private String id;
	
	private String recipient;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiresOn;
	
	@CreationTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
    private Date createdAt;

	private String jsonId;

}
