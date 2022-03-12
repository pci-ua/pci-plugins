# Comment faire un rapport de bug
Pour permettre une résolution de bug rapide, merci de transmettre ces informations :
- Version du plugin
- Version de Spigot
- Version de Java
- Informations sur le système d'exploitation (nom et version)

# Style de code pour les contributions
```java
private int _privateField;
public static int s_staticField;
private static int s_privateStaticField;

/**
 * Javadoc
 */
public void functionName(int argNumberOne, String argNumberTwo) {
    this._privateField++; // Not _privateField without 'this.' before
}
```