package com.journaler.api.security

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Email

@Entity
@Table(name = "user")
@JsonInclude(JsonInclude.Include.NON_NULL)
open class User(
        @Id
        @GeneratedValue(generator = "uuid2")
        @GenericGenerator(name = "uuid2", strategy = "uuid2")
        @Column(columnDefinition = "varchar(36)")
        open var id: String = "",

        @Column(unique = true, nullable = false)
        @NotNull
        @Email
        open var email: String = "",

        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @NotBlank
        open var pwd: String = "",

        @NotBlank
        open var firstName: String = "",

        @NotBlank
        open var lastName: String = "",

        open var roles: String = "",
        open var enabled: Boolean = true,
        open var accountNonExpired: Boolean = true,
        open var accountNonLocked: Boolean = true,
        open var credentialsNonExpired: Boolean = true,

        @CreationTimestamp
        open var created: Date = Date(),

        @UpdateTimestamp
        open var modified: Date = Date()
) : UserDetails {

    /**
     * We need empty constructor for SecurityInitializationTest and Hibernate.
     */
    constructor() : this(
            "", "", "", "", "", "", true, true, true, true, Date(), Date()
    )

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()
        roles
                .split(",")
                .forEach { it ->
                    authorities.add(
                            SimpleGrantedAuthority(
                                    it.trim()
                            )
                    )
                }
        return authorities
    }

    override fun isEnabled() = enabled

    override fun getUsername() = email

    override fun isCredentialsNonExpired() = credentialsNonExpired

    override fun getPassword() = pwd

    override fun isAccountNonExpired() = accountNonExpired

    override fun isAccountNonLocked() = accountNonLocked

}
