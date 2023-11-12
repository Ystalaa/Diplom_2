import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String email;
    private String password;
    private String name;
}