## Preparação do Ambiente

Para configurar o ambiente, siga os passos abaixo:

1. **Baixar e instalar os seguintes componentes:**
   - [Java Development Kit](https://aws.amazon.com/corretto/)
   - [SUMO - Simulation of Urban MObility](https://eclipse.dev/sumo/)
   - [Apache Maven](https://maven.apache.org/download.cgi)
   - [Visual Studio Code](https://code.visualstudio.com/Download)

2. **Clonar o projeto**
   Abra o terminal e navegue até a pasta que deseja clonar o projeto
   ```bash
   git clone https://github.com/Whilker-Silva/trabalho-final-automacao-avancada.git
   ```

3. **Abrir o projeto no Visual Studio Code:**

    Dentro da pasta clonada, abra um terminal e digite:
   ```bash
   code .
   ```

4. **Abrir o terminal e executar os seguintes comandos:**
   ```bash
   mvn install:install-file -Dfile="./lib/junit.jar" -DgroupId="junit" -DartifactId="junit" -Dversion="junit" -Dpackaging="jar" -DgeneratePom=true

   mvn install:install-file -Dfile="./lib/libsumo-1.18.0-sources.jar" -DgroupId="libsumo-1.18.0-sources" -DartifactId="libsumo-1.18.0-sources" -Dversion="libsumo-1.18.0-sources" -Dpackaging="jar" -DgeneratePom=true

   mvn install:install-file -Dfile="./lib/libsumo-1.18.0.jar" -DgroupId="libsumo-1.18.0" -DartifactId="libsumo-1.18.0" -Dversion="libsumo-1.18.0" -Dpackaging="jar" -DgeneratePom=true

   mvn install:install-file -Dfile="./lib/libtraci-1.18.0-sources.jar" -DgroupId="libtraci-1.18.0-sources" -DartifactId="libtraci-1.18.0-sources" -Dversion="libtraci-1.18.0-sources" -Dpackaging="jar" -DgeneratePom=true

   mvn install:install-file -Dfile="./lib/libtraci-1.18.0.jar" -DgroupId="libtraci-1.18.0" -DartifactId="libtraci-1.18.0" -Dversion="libtraci-1.18.0" -Dpackaging="jar" -DgeneratePom=true

   mvn install:install-file -Dfile="./lib/lisum-core.jar" -DgroupId="lisum-core" -DartifactId="lisum-core" -Dversion="lisum-core" -Dpackaging="jar" -DgeneratePom=true

   mvn install:install-file -Dfile="./lib/lisum-gui.jar" -DgroupId="lisum-gui" -DartifactId="lisum-gui" -Dversion="lisum-gui" -Dpackaging="jar" -DgeneratePom=true

   mvn install:install-file -Dfile="./lib/TraaS.jar" -DgroupId="TraaS" -DartifactId="TraaS" -Dversion="TraaS" -Dpackaging="jar" -DgeneratePom=true

   mvn clean install
   ```

5. **Reinicie o VS Code e execute o programa:**

   Se tudo tiver sido feito corretamente, o programa já irá abrir o SUMO e abrir uma tela de simulação.