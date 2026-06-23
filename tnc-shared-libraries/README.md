# TNC Shared Libraries

This repository contains reusable libraries for TNC microservices, following a modular approach where each library focuses on a specific concern.

## 📚 **Available Libraries**

### 🔐 **tnc-security-lib**
- **Purpose**: Security utilities, JWT handling, authentication, and authorization
- **GroupId**: `com.tnc.security`
- **ArtifactId**: `tnc-security-lib`
- **Version**: `1.0.0`

### 🛡️ **tnc-resilience-lib**
- **Purpose**: Circuit breaker, retry, timeout, and bulkhead patterns
- **GroupId**: `com.tnc.resilience`
- **ArtifactId**: `tnc-resilience-lib`
- **Version**: `1.0.0`

### 📖 **tnc-swagger-lib**
- **Purpose**: OpenAPI/Swagger documentation configuration
- **GroupId**: `com.tnc.swagger`
- **ArtifactId**: `tnc-swagger-lib`
- **Version**: `1.0.0`

### 🐳 **tnc-docker-lib**
- **Purpose**: Docker templates and build scripts
- **GroupId**: `com.tnc.docker`
- **ArtifactId**: `tnc-docker-lib`
- **Version**: `1.0.0`

## 🚀 **Quick Start**

### **1. Build All Libraries**
```bash
cd tnc-shared-libraries
mvn clean install -DskipTests
```

### **2. Use in Microservices**
Add to your microservice's `pom.xml`:

```xml
<dependency>
    <groupId>com.tnc.security</groupId>
    <artifactId>tnc-security-lib</artifactId>
    <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>com.tnc.resilience</groupId>
    <artifactId>tnc-resilience-lib</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 🏗️ **Architecture**

### **Modular Design**
- Each library is **independent** and can be used separately
- **Single Responsibility Principle** - each library has one clear purpose
- **Loose Coupling** - libraries don't depend on each other
- **Easy Maintenance** - changes to one library don't affect others

### **Benefits Over Monolithic Approach**
- ✅ **Selective Dependencies** - only include what you need
- ✅ **Independent Versioning** - update libraries separately
- ✅ **Faster Builds** - smaller dependency trees
- ✅ **Better Testing** - test each library in isolation
- ✅ **Easier Debugging** - clear separation of concerns

## 📦 **Installation Options**

### **Option 1: Local Maven Repository (Development)**
```bash
mvn clean install -DskipTests
```

### **Option 2: Private Maven Repository (Production)**
```bash
mvn clean deploy
```

### **Option 3: Relative Path (Development)**
```xml
<dependency>
    <groupId>com.tnc.security</groupId>
    <artifactId>tnc-security-lib</artifactId>
    <version>1.0.0</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/../tnc-shared-libraries/tnc-security-lib/target/tnc-security-lib-1.0.0.jar</systemPath>
</dependency>
```

## 🔧 **Development**

### **Adding New Libraries**
1. Create new directory: `tnc-[name]-lib`
2. Add `pom.xml` with proper `groupId` and `artifactId`
3. Implement library code
4. Add to documentation

### **Updating Libraries**
1. Make changes to library code
2. Update version in `pom.xml`
3. Run `mvn clean install`
4. Update microservice dependencies

## 📋 **Migration Guide**

### **From Duplicated Code**
1. **Identify** duplicated code in microservices
2. **Extract** common functionality to appropriate library
3. **Update** microservice dependencies
4. **Remove** duplicated code from microservices
5. **Test** thoroughly

### **From Monolithic Shared Library**
1. **Split** monolithic library into focused modules
2. **Update** microservice dependencies to use specific libraries
3. **Remove** unused dependencies
4. **Test** each microservice

## 🎯 **Best Practices**

- **Keep libraries focused** - one concern per library
- **Use semantic versioning** - MAJOR.MINOR.PATCH
- **Document breaking changes** - maintain changelog
- **Test thoroughly** - unit and integration tests
- **Keep dependencies minimal** - avoid unnecessary bloat

## 📞 **Support**

For questions or issues:
- Check individual library documentation
- Review migration guides
- Test in development environment first
