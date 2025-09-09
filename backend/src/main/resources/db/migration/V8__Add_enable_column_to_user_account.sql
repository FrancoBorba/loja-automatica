-- 1. Adiciona a coluna sem restrições
ALTER TABLE user_account
ADD COLUMN enabled BOOLEAN;

-- 2. Atualiza os dados antigos com o valor desejado
UPDATE user_account
SET enabled = true;

-- 3. Torna a coluna obrigatória e com valor padrão para novos registros
ALTER TABLE user_account
ALTER COLUMN enabled SET NOT NULL;