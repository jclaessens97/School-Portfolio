package be.kdg.rideservice.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

/**
 * Implements custom auto naming strategy for Hibernate.
 * This implementation converts the java naming convention (camelCase) to the naming convention
 * of the already existing database (PascalCase)
 */
public class CustomPhysicalNamingStrategy implements PhysicalNamingStrategy {
    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convertToPascalCase(name);
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convertToPascalCase(name);
    }

    @Override
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convertToPascalCase(name);
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convertToPascalCase(name);
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
        return convertToPascalCase(name);
    }

    private Identifier convertToPascalCase(Identifier identifier) {
        if (identifier == null) {
            return null;
        }

        final String newName = identifier.getText().substring(0, 1).toUpperCase() + identifier.getText().substring(1);
        return Identifier.toIdentifier(newName);
    }
}
