package ru.test.postprocessor;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.test.annotation.SingularsFromMetaModel;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Component
@Log4j2
public class MetaModelMapPostProcessor implements BeanPostProcessor {

    public MetaModelMapPostProcessor(@Autowired EntityManagerFactory factory) {
        // необходимо взять метамодель, тк в ином случае она подгружается лениво (как я понял)
        // и на момент выполнения процессинга статические поля классов модели могут еще быть не инициализированы
        factory.getMetamodel();
    }


    // процессит класс мета модели и заполняет мапу с атрибутами
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> target = bean.getClass();

        final Field[] fields = Stream.of(target.getFields(),
                target.getDeclaredFields()).flatMap(Stream::of).toArray(Field[]::new);

        for (Field field : fields) {
            if (field.getAnnotation(SingularsFromMetaModel.class) != null) {
                log.debug("current processed bean {}", beanName);
                final SingularsFromMetaModel annotation = field.getAnnotation(SingularsFromMetaModel.class);
                final Class<?> metaModelClass = annotation.target();

                if (metaModelClass.isAnnotationPresent(StaticMetamodel.class)) {
                    final StaticMetamodel modelClassAnnotation = metaModelClass.getAnnotation(StaticMetamodel.class);
                    if (field.getType().equals(Map.class)) {
                        ParameterizedType type = (ParameterizedType) field.getGenericType();
                        final Type[] actualTypeArguments = type.getActualTypeArguments();

                        try {
                            if (actualTypeArguments.length == 2 &&
                                    String.class.equals(Class
                                            .forName(actualTypeArguments[0].getTypeName())
                                    ) &&
                                    SingularAttribute.class
                                            .equals(Class
                                                    .forName(
                                                            ((ParameterizedType) actualTypeArguments[1])
                                                                    .getRawType()
                                                                    .getTypeName()))) {

                                if (((ParameterizedType) actualTypeArguments[1]).getActualTypeArguments()[0]
                                        .getTypeName()
                                        .equals(modelClassAnnotation.value().getName())) {

                                    final Field[] fieldsFromStaticMetadataClass = metaModelClass.getFields();
                                    Map<String, Object> map = new HashMap<>();
                                    for (Field fromStaticMetadataClass : fieldsFromStaticMetadataClass) {

                                        if (SingularAttribute.class.equals(fromStaticMetadataClass.getType())) {
                                            final ParameterizedType typeOfFieldFromMM =
                                                    (ParameterizedType) fromStaticMetadataClass.getGenericType();

                                            fromStaticMetadataClass.setAccessible(true);
                                            final Object o = fromStaticMetadataClass.get(null);
                                            map.put(fromStaticMetadataClass.getName(), o);
                                        }
                                    }
                                    field.setAccessible(true);
                                    field.set(bean, Collections.unmodifiableMap(map));
                                }
                            }
                        } catch (Exception e) {
                            throw new RuntimeException("fail while process metamodel");
                        }
                    }
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
