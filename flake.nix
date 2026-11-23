{
  description = "ardan-born — Paper plugin dev environment";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
    flake-utils.url = "github:numtide/flake-utils";
  };

  outputs = { nixpkgs, flake-utils, ... }:
    flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs { inherit system; };
        jdk = pkgs.jdk25;
        # Gradle 9.1+ is required to run on Java 25.
        gradle = pkgs.gradle_9.override { java = jdk; };
      in
      {
        devShells.default = pkgs.mkShell {
          packages = [ jdk gradle pkgs.git pkgs.hugo pkgs.pagefind ];

          JAVA_HOME = "${jdk}/lib/openjdk";

          shellHook = ''
            # Keep Gradle's caches local to the project instead of ~/.gradle
            export GRADLE_USER_HOME="$PWD/.gradle-home"
            echo "java:   $(java -version 2>&1 | head -1)"
            echo "gradle: $(gradle --version 2>/dev/null | grep '^Gradle')"
          '';
        };
      });
}
