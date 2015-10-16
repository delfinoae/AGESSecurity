package br.ages.crud.command;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import br.ages.crud.bo.ArquivoBO;
import br.ages.crud.bo.ProjetoBO;
import br.ages.crud.model.Projeto;
import br.ages.crud.model.Status;
import br.ages.crud.util.Constantes;
import br.ages.crud.util.MensagemContantes;

@MultipartConfig
public class AdicionaProjetoCommand implements Command {
	
	private String proxima;
	
	private ProjetoBO projetoBO;
	
	private ArquivoBO ArquivoBO;
	
	@Override
	public String execute(HttpServletRequest request) {
		projetoBO =  new ProjetoBO();
		proxima = "project/addProject.jsp";
		
		String nome = request.getParameter("nome");
		String equipe = request.getParameter("equipe");
		String status = request.getParameter("status");
		String workspace = request.getParameter("workspace");
		
		
		try{
			Projeto projeto = new Projeto();
			projeto.setNomeProjeto(nome);
			//projeto.setEquipe(equipe);
			projeto.setStatus(Status.valueOf(status));
			projeto.setWorkspace(workspace);

			boolean isValido = projetoBO.validarProjeto(projeto);			
			
			if (!isValido) {
				request.setAttribute("msgErro", MensagemContantes.MSG_ERR_PROJETO_DADOS_INVALIDOS);
			} else {
				//come�a o tro�o do upload
				ArquivoBO = new ArquivoBO();
				
				Part arquivo = request.getPart("arquivo");
				
				boolean tamanhoValido = ArquivoBO.validaTamanho(arquivo, Constantes.PROJETO_ARQUIVO_MAX_BYTES);
				boolean extensaoValida = ArquivoBO.validaExtensao(arquivo, Constantes.PROJETO_FILE_EXT);
				
				if(!tamanhoValido || !extensaoValida){
					request.setAttribute("msgErro", MensagemContantes.MSG_ERR_PROJETO_ARQUIVO_INVALIDO.replace("?", String.valueOf(Constantes.PROJETO_ARQUIVO_MAX_BYTES)));
				} else{
					ArquivoBO.uploadArquivo(arquivo, nome);
					
					projetoBO.cadastrarProjeto(projeto);
					
					proxima = "main?acao=listProject";
					request.setAttribute("msgSucesso", MensagemContantes.MSG_SUC_CADASTRO_PROJETO.replace("?", projeto.getNomeProjeto()));
				}
				
			}
			
		}catch(Exception e){
			request.setAttribute("msgErro", e.getMessage());
		}
			
		return proxima;
	}

}
