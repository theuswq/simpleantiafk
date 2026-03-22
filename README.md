# SimpleAntiAFK

<div align="center">

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.20+-green.svg)
![Paper](https://img.shields.io/badge/Paper-1.20.1-orange.svg)
![Java](https://img.shields.io/badge/java-17-red.svg)

**Um plugin simples, profissional e eficiente para detectar e punir jogadores AFK em servidores Minecraft**

[Características](#-características) • [Instalação](#-instalação) • [Configuração](#-configuração) • [Permissões](#-permissões)

</div>

---

## 📋 Sobre

O **SimpleAntiAFK** é um plugin desenvolvido para servidores Minecraft (Paper/Spigot) que detecta automaticamente jogadores que estão AFK e aplica ações configuráveis após um período de inatividade. 

Desenvolvido com foco em **performance**, **simplicidade** e **profissionalismo**, este plugin é ideal para servidores SMP e Survival que precisam gerenciar jogadores inativos de forma automática.

## ✨ Características

- ✅ **Detecção Inteligente**: Monitora movimento e rotação de câmera
- ✅ **Totalmente Configurável**: Tempo de AFK, ações e mensagens personalizáveis
- ✅ **Sistema de Avisos**: Notifica jogadores antes da punição
- ✅ **Múltiplas Ações**: Suporte para kick automático ou execução de comandos
- ✅ **Sistema de Permissões**: Bypass para jogadores privilegiados
- ✅ **Leve e Eficiente**: Otimizado para não impactar a performance do servidor
- ✅ **Thread-Safe**: Implementação segura para ambientes multi-threaded
- ✅ **Compatível com Reload**: Suporta `/antiafk reload` sem reiniciar o servidor

## 🚀 Instalação

### Requisitos

- **Minecraft**: Versão 1.20 ou superior
- **Servidor**: Paper ou Spigot
- **Java**: Versão 17 ou superior

### Passos

1. Baixe a versão mais recente do plugin na seção [Releases](https://github.com/theuswq/simpleantiafk/releases)
2. Coloque o arquivo `SimpleAntiAFK-1.0.0.jar` na pasta `plugins` do seu servidor
3. Reinicie o servidor ou use `/reload` (não recomendado em produção)
4. Configure o arquivo `config.yml` conforme suas necessidades
5. Use `/antiafk reload` para aplicar as mudanças

## ⚙️ Configuração

O arquivo `config.yml` será criado automaticamente na primeira execução. Aqui está um exemplo de configuração:

```yaml
afk:
  # Tempo máximo em segundos antes de aplicar a ação (padrão: 300 = 5 minutos)
  max-time-seconds: 300
  
  # Tempo em segundos antes da punição para enviar aviso (padrão: 30)
  warning-seconds: 30
  
  # Ação a ser executada: KICK ou COMMAND
  action: KICK
  
  # Comando a ser executado (apenas se action for COMMAND)
  # Use {player} como placeholder para o nome do jogador
  command: "spawn {player}"
  
  # Mensagem de kick (apenas se action for KICK)
  kick-message: "&cVocê foi kickado por ficar AFK por muito tempo!"

# Mensagens personalizáveis
messages:
  warning: "&e⚠ Atenção! Você está AFK há {time} segundos. Você será {action} em breve!"
  kicked: "&cVocê foi kickado por ficar AFK."
  reload-success: "&aConfiguração recarregada com sucesso!"
  reload-error: "&cErro ao recarregar configuração. Verifique o console."
  status-online: "&aPlugin ativo. {count} jogador(es) sendo monitorado(s)."
  status-offline: "&cPlugin desativado."
```

### Exemplos de Configuração

#### Kick após 5 minutos de AFK
```yaml
afk:
  max-time-seconds: 300
  warning-seconds: 30
  action: KICK
  kick-message: "&cVocê foi desconectado por inatividade!"
```

#### Teleportar para spawn após 10 minutos
```yaml
afk:
  max-time-seconds: 600
  warning-seconds: 60
  action: COMMAND
  command: "spawn {player}"
```

#### Executar comando personalizado
```yaml
afk:
  max-time-seconds: 180
  warning-seconds: 20
  action: COMMAND
  command: "afkroom {player}"
```

## 🔐 Permissões

| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `simpleantiafk.admin` | Permite usar comandos administrativos (`/antiafk`) | `op` |
| `simpleantiafk.bypass` | Ignora a detecção de AFK | `false` |

### Exemplos de Uso

```yaml
# Dar bypass para VIPs
permissions:
  vip:
    permissions:
      - simpleantiafk.bypass

# Dar permissão de admin para moderadores
permissions:
  moderator:
    permissions:
      - simpleantiafk.admin
```

## 📝 Comandos

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/antiafk reload` | Recarrega a configuração do plugin | `simpleantiafk.admin` |
| `/antiafk status` | Mostra o status do plugin e quantos jogadores estão sendo monitorados | `simpleantiafk.admin` |

**Aliases**: `/aafk`, `/safk`

## 🎯 Como Funciona

1. **Monitoramento**: O plugin monitora continuamente todos os jogadores online
2. **Detecção de Movimento**: Verifica se o jogador se moveu ou girou a câmera
3. **Contagem de Tempo**: Calcula o tempo desde a última atividade
4. **Aviso**: Envia uma mensagem de aviso quando o tempo de aviso é atingido
5. **Ação**: Aplica a ação configurada (kick ou comando) quando o tempo máximo é excedido

### Detalhes Técnicos

- **Eventos Utilizados**: `PlayerMoveEvent`, `PlayerJoinEvent`, `PlayerQuitEvent`
- **Scheduler**: Verificação a cada segundo (20 ticks)
- **Estrutura de Dados**: `HashMap<UUID, AFKData>` para armazenamento eficiente
- **Thread Safety**: Implementação thread-safe para ambientes concorrentes

## 📸 Screenshots

### Exemplo de Aviso
```
⚠ Atenção! Você está AFK há 270 segundos. Você será kickado em breve!
```

### Exemplo de Kick
```
Você foi kickado por ficar AFK por muito tempo!
```

### Comando de Status
```
/aafk status
Plugin ativo. 15 jogador(es) sendo monitorado(s).
```

## 🛠️ Desenvolvimento

### Estrutura do Projeto

```
src/
 └─ main/
    ├─ java/
    │  └─ me/theuswq/simpleantiafk/
    │     ├─ SimpleAntiAFKPlugin.java    # Classe principal
    │     ├─ afk/
    │     │  ├─ AFKManager.java          # Gerenciador de AFK
    │     │  └─ AFKData.java             # Dados do jogador
    │     ├─ listeners/
    │     │  └─ PlayerActivityListener.java  # Eventos do jogador
    │     └─ utils/
    │        └─ TimeUtils.java           # Utilitários de tempo
    └─ resources/
       ├─ plugin.yml                     # Configuração do plugin
       └─ config.yml                     # Configuração do usuário
```

### Compilação

```bash
# Clonar o repositório
git clone https://github.com/theuswq/simpleantiafk.git
cd simpleantiafk

# Compilar com Maven
mvn clean package

# O arquivo JAR estará em: target/SimpleAntiAFK-1.0.0.jar
```

## 🗺️ Roadmap

- [ ] Suporte para múltiplas ações simultâneas
- [ ] Integração com PlaceholderAPI
- [ ] Métricas e estatísticas de AFK
- [ ] Suporte para diferentes mundos com configurações diferentes
- [ ] Comando para verificar status AFK de um jogador específico
- [ ] API para desenvolvedores integrarem com outros plugins

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para:

1. Fazer um Fork do projeto
2. Criar uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abrir um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👤 Autor

**theuswq**

- GitHub: [@theuswq](https://github.com/theuswq)
- Projeto: [SimpleAntiAFK](https://github.com/theuswq/simpleantiafk)

## 🙏 Agradecimentos

- Comunidade Paper/Spigot pela excelente documentação
- Todos os contribuidores e usuários do plugin

## 📞 Suporte

Se você encontrar algum bug ou tiver sugestões, por favor abra uma [Issue](https://github.com/theuswq/simpleantiafk/issues) no GitHub.

---

<div align="center">

⭐ Se este projeto foi útil para você, considere dar uma estrela!

Feito com ❤️ por theuswq

</div>

