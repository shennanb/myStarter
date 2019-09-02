package code;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class RabbitMqQueue<T> {

    @NotNull
    private String queue;

    @Valid
    @NotNull
    private T content;

    public String getQueue(){
        return queue;
    }

    public T getContent(){
        return content;
    }

}
