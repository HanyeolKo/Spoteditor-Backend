package com.spoteditor.backend.modules.notification.entity;

import com.spoteditor.backend.global.entity.BaseEntity;
import com.spoteditor.backend.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Long id;

	@Column(nullable = false)
	private String message;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;

	@Column(nullable = false)
	@ColumnDefault("false")
	private boolean isRead = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_id")
	private User fromUser;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_id")
	private User toUser;

	@Builder
	private Notification(String message, NotificationType type, User fromUser, User toUser) {
		this.message = message;
		this.notificationType = type;
		this.fromUser = fromUser;
		this.toUser = toUser;
	}

	public void read() {
		this.isRead = true;
	}
}
