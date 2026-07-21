# 1. Ändere die Berechtigung in Git auf "ausführbar"
git update-index --chmod=+x gradlew

# 2. Commite die Änderung
git commit -m "Fix: gradlew Ausführungsrechte für GitHub Actions hinzugefügt"

# 3. Pushe den Fix zu GitHub
git push
